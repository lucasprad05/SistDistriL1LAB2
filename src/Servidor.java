/**
 * Laboratorio 2 de Sistemas Distribuidos
 * Sistemas Distribuídos - SDCO8A- 2025/1
 * Professor: Lucio Agostinho Rocha
 * 
 * Ana Carolina Ribeiro Miranda - 2208407
 * Lucas Castilho Pinto Prado - 
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

                String resposta = "";
                
                				//{"method":"read","args":[""]}
                if (mensagem.contains("\"method\":\"read\"")) {
                	// Caso cliente solicite uma fortuna aleatória
                    resposta = selecionarFortuna();
                    						//{"method":"write","args":["Nova fortuna"]}
                } else if (mensagem.contains("\"method\":\"write\"")) {
                	// Caso Cliente solicite adicionar uma nova fortuna
                	int i1 = mensagem.indexOf("[\"") + 2;
                    int i2 = mensagem.indexOf("\"]");
                    String novaFortuna = mensagem.substring(i1, i2);
                    adicionarFortuna(novaFortuna);
                    resposta = "Fortuna adicionada com sucesso.";
                } else {
                    resposta = "Opção inválida.";
                }
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
                if (linha.equals("%")) { // Cada fortuna é separada por "%"
                    fortunas.add(fortuna.toString());
                    fortuna.setLength(0);  // Reinicia o buffer
                } else {
                    fortuna.append(linha + "\n");
                }
            }
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
    public void adicionarFortuna(String fortuna) {
        try {
            BufferedWriter escritor = new BufferedWriter(new FileWriter("src/fortune-br.txt", true));
            escritor.write(fortuna); // Escreve a nova fortuna
            escritor.newLine();
            escritor.write("%"); // Separador de fortuna
            escritor.newLine();
            escritor.close();
        } catch (Exception e) {
            System.out.println("Erro ao escrever no arquivo: " + e.getMessage());
        }//try cathc
    }//adicionarFortuna

    public static void main(String[] args) {
        new Servidor().iniciar();
    }//void
}//Servidor
