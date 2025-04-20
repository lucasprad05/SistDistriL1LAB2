
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

            // Recebe do usuario algum valor (Leitor de entrada do teclado)
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            // Receber o valor
            int opcao = 0;

            // Enquanto o usuário desejar permanecer no menu Cliente
            do {
                // Exibe o menu
                System.out.println("\nBem-vindo Cliente! 😁");
                System.out.println(" ==== Menu de Opções: ==== ");
                System.out.println("1 - Fortuna aleatória");
                System.out.println("2 - Adicionar nova fortuna");
                System.out.println("3 - Finalizar Conexão");
                System.out.print("Digite apenas o número da sua opção: ");

                try {
                    // Le a opção digitada pelo usuário
                    String opcaoStr = br.readLine();
                    opcao = Integer.parseInt(opcaoStr);

                    String json = "";

                    // Solicita uma fortuna aleatório
                    if (opcao == 1) {
                        json = "{\"method\":\"read\",\"args\":[\"\"]}";

                        // Adiciona nova fortuna ao Servidor
                    } else if (opcao == 2) {
                        System.out.println("Digite a nova fortuna (digite % para finalizar):"); //O servidor adiciona o '%', aqui usamos so para escrever mais linhas e controlar o fim.
                        String novaFortuna = "";
                        String linha;
                        // Le várias linhas até o usuário digitar "%"
                        while (!(linha = br.readLine()).equals("%")) {
                            novaFortuna += linha + "\n";
                        }// while


                        json = "{\"method\":\"write\",\"args\":[\"" + novaFortuna + "\"]}";

                        // Finaliza o Cliente
                    } else if (opcao == 3) {

                        // Envia um aviso para o servidor antes de encerrar
                        json = "{\"method\":\"close\",\"args\":[\"\"]}";
                        saida.writeUTF(json);
                        System.out.println("Conexão do Cliente finalizada.");
                        // Sai do loop
                        break;

                    } else {
                        System.out.println("Opção inválida!");
                        continue;
                    }//else

                    // O valor [mensagem JSON] é enviado ao servidor
                    saida.writeUTF(json);

                    // Recebe-se o resultado do servidor
                    String resultado = entrada.readUTF();

                    // Exibe a resposta recebida
                    System.out.println("Resposta do servidor:");
                    System.out.println(resultado);


                } catch (NumberFormatException e) {
                    System.err.println("Por favor, digite uma opção válida!");
                } catch (Exception e) {
                    System.err.println("Ocorreu um erro, tente novamente!");
                }//try catch

            } while (opcao != 3); //do

            // Fecha tudo quando sair do menu
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
