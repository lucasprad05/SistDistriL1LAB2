/**
 * Laboratorio 2 de Sistemas Distribuidos
 * Sistemas Distribuídos - SDCO8A- 2025/1
 * Professor: Lucio Agostinho Rocha
 * 
 * Ana Carolina Ribeiro Miranda - 2208407
 * Lucas Castilho Pinto Prado - 2367980
 */

import java.io.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.ArrayList;

public class Servidor {

    private static Socket socket;
    private static ServerSocket server;

    private static DataInputStream entrada;
    private static DataOutputStream saida;

    private int porta = 1025;
    
    private String metodo = "";
    private String argumento = "";


    public void iniciar() {
    	
        System.out.println("Servidor iniciado na porta: " + porta);
        try {

			// Criar porta de recepcao
        	server = new ServerSocket(porta);
            socket = server.accept(); // Processo fica bloqueado, à espera de conexões
            
            // Criar os fluxos de entrada e saida
            entrada = new DataInputStream(socket.getInputStream());
            saida = new DataOutputStream(socket.getOutputStream());
            
            // Processa mensagens enquanto o cliente estiver conectado
            while (true) {
                String mensagem = entrada.readUTF();
               

                if (mensagem == null || mensagem.trim().isEmpty()) {
                    break;
                }//if

                // Analisa o que o Cliente solicitou
                parser(mensagem); 
                
                String resposta = "";

                if (metodo.equals("read")) {
                	// Caso cliente solicite uma fortuna aleatória
                    resposta = selecionarFortuna();
                    				
                } else if (metodo.equals("write")) {
                	// Caso Cliente solicite adicionar uma nova fortuna
                    adicionarFortuna(argumento);
                    resposta = "Fortuna adicionada com sucesso.";
                
                } else if (metodo.equals("close")) {
                	// Caso finalize a conexão com o Cliente
                	resposta = "Conexão encerrada pelo cliente.";
                    saida.writeUTF(resposta);
                    break; // Fecha a conexão desse cliente, mas mantem do servidor 
                
                }else {
                	// Caso Digite algo fora do esperado
                	resposta = "Opção inválida.";
                }//else
                
                // Envio dos dados (resultado)
                saida.writeUTF(resposta);

            }//while
            
            // Fecha a conexão após sair do loop
            socket.close();

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }//try catch
    }//iniciar

    // Ler uma fortuna aleatória do arquivo "fortune-br.txt"
    public String selecionarFortuna() {
        ArrayList<String> fortunas = new ArrayList<String>();
        
        try {
            BufferedReader leitor = new BufferedReader(new FileReader("src/fortune-br.txt"));
            String linha;
            StringBuffer fortuna = new StringBuffer();
            
            while ((linha = leitor.readLine()) != null) {
                if (linha.equals("%")) { // Fortuna separada por "%"
                    fortunas.add(fortuna.toString());
                    fortuna.setLength(0);  // Reinicia o buffer
                } else {
                    fortuna.append(linha + "\n");
                }//else
            }//while
            
            leitor.close();
            
        } catch (Exception e) {
            System.out.println("Erro na leitura do arquivo: " + e.getMessage());
        }// try catch
        
        // Retorna uma fortuna aleatória 
        if (fortunas.size() > 0) {
            Random random = new Random();
            return fortunas.get(random.nextInt(fortunas.size()));
        } else { 
        	//Retorna uma mensagem caso não haja fortunas
            return "Nenhuma fortuna encontrada.";
        }//else
    }//selecionarFortuna

    // Adicionar uma nova fortuna no arquivo "fortune-br.txt"
    public void adicionarFortuna(String argumento) {
        try {
            BufferedWriter fortuna = new BufferedWriter(new FileWriter("src/fortune-br.txt", true));
            fortuna.write(argumento); // Escreve a nova fortuna
            fortuna.newLine();
            fortuna.write("%");  // Marca o fim da fortuna
            fortuna.newLine();
            fortuna.close();
        } catch (Exception e) {
            System.out.println("Erro ao escrever no arquivo: " + e.getMessage());
        }//try cathc
    }//adicionarFortuna

 // Descobre o método (read, write, close) 
    public void parser(String mensagem) {
    	// Limpa os valores anteriores 
        String metodo = "";
        String argumento = "";

        // Verifica se é leitura
        if (mensagem.contains("\"method\":\"read\"")) {
            metodo = "read";
            
         // Verifica se é escrita    
        } else if (mensagem.contains("\"method\":\"write\"")) {
            metodo = "write";

            // Pega o que está dentro do args
            int i1 = mensagem.indexOf("[\"") + 2;
            int i2 = mensagem.indexOf("\"]");
            argumento = mensagem.substring(i1, i2);
            
        // Verifica se o cliente quer encerrar a conexão
        } else if (mensagem.contains("\"method\":\"close\"")) {
            metodo = "close";
        }//else
    }//parser
    
    
    public static void main(String[] args) {
        new Servidor().iniciar();
    }//void
}//Servidor
