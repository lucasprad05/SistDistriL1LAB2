
/**
 * Laboratorio 2 de Sistemas Distribuidos
 * Sistemas Distribuídos - SDCO8A- 2025/1
 * Professor: Lucio Agostinho  Rocha
 * 
 * Ana Carolina Ribeiro Miranda - 2208407
 * Lucas Castilho Pinto Prado - 
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Cliente {
    
    private static Socket socket;
    private static DataInputStream entrada;
    private static DataOutputStream saida;
    
    private int porta=1025;
    
    public void iniciar(){
    	System.out.println("Cliente iniciado na porta: "+porta);
    	
    	try {
            
            socket = new Socket("127.0.0.1", porta);
            
            entrada = new DataInputStream(socket.getInputStream());
            saida = new DataOutputStream(socket.getOutputStream());
            
            //Recebe do usuario algum valor
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            
            // Receber o valor utilizando Json            
            int opcao_usuario = 0;

            // Enquanto o usuário desejar permanecer no menu Cliente
            do {
                // Exibe o menu para o usuário
                System.out.println("\nBem-vindo ao Software Cliente!");
                System.out.println(" ==== Menu de Opções: ==== ");
                System.out.println("1 - Obter fortuna aleatória");
                System.out.println("2 - Adicionar nova fortuna ao banco de dados");
                System.out.println("3 - Finalizar Conexão");
                System.out.print("Digite sua opção: ");

                try {
                    String opcaoStr = br.readLine();
                    opcao_usuario = Integer.parseInt(opcaoStr);

                    String json = "";
                    
                    // Solicita uma fortuna aleatório
                    if (opcao_usuario == 1) {
                        json = "{\"method\":\"read\",\"args\":[\"\"]}\n";
                    
                    // Envia nova fortuna ao servidor
                    } else if (opcao_usuario == 2) {
                        System.out.println("Digite a nova fortuna:");
                        String novaFortuna = br.readLine();
                        json = "{\"method\":\"write\",\"args\":[\"" + novaFortuna + "\"]}\n";
                    
                    // Finaliza o programa
                    } else if (opcao_usuario == 3) { 
                        System.out.println("Encerrando conexão...");
                        break;
                    } else { 
                        System.out.println("Opção inválida!");
                        continue;
                    }//else
            
		            //O valor [mensagem JSON] é enviado ao servidor
                    saida.writeUTF(json);
		                    
		            //Recebe-se o resultado do servidor
		            String resultado = entrada.readUTF();
		            
		            //Mostra o resultado na tela
		            System.out.println("Resposta do servidor:");
		            System.out.println(resultado);
		            
		           
		            
                } catch (NumberFormatException e) {
                    System.err.println("Por favor, digite uma opção válida!");
                } catch (Exception e) {
                    System.err.println("Ocorreu um erro, tente novamente!");
                }//try catch
                
        	} while (opcao_usuario != 3); //do
            
            socket.close();
            br.close();
          
	    	}catch(Exception e) {
	          	e.printStackTrace();
	          }//catch
    }//Iniciar
          
    
    public static void main(String[] args) {
        new Cliente().iniciar();
    }//main
    
}//Cliente
