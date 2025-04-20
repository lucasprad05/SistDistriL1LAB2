/**
 * Laboratorio 2 de Sistemas Distribuidos
 * Sistemas Distribuídos - SDCO8A- 2025/1
 * Professor: Lucio Agostinho Rocha
 *
 * Ana Carolina Ribeiro Miranda - 2208407
 * Lucas Castilho Pinto Prado - 2367980
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.ArrayList;

public class Servidor {

    private ServerSocket server;
    private int porta = 4000;

    private String metodo = "";
    private String argumento = "";

    public void iniciar() {
        System.out.println("Servidor iniciado na porta: " + porta);

        try {
            server = new ServerSocket(porta);

            while (true) {
                Socket socketCliente = server.accept();
                System.out.println("Cliente conectado: " + socketCliente.getInetAddress());

                // cria uma thread pra tratar esse cliente
                new Thread(() -> tratarCliente(socketCliente)).start();
            }

        } catch (Exception e) {
            System.out.println("Erro no servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Cada cliente é tratado aqui
    public void tratarCliente(Socket socketCliente) {
        try {
            DataInputStream entrada = new DataInputStream(socketCliente.getInputStream());
            DataOutputStream saida = new DataOutputStream(socketCliente.getOutputStream());

            while (true) {
                String mensagem = entrada.readUTF();

                if (mensagem == null || mensagem.trim().isEmpty()) {
                    break;
                }

                parser(mensagem);

                String resposta = "";

                switch (metodo) {
                    case "read":
                        resposta = selecionarFortuna();
                        break;
                    case "write":
                        adicionarFortuna(argumento);
                        resposta = "Fortuna adicionada com sucesso.";
                        break;
                    case "close":
                        resposta = "Conexão encerrada pelo cliente.";
                        saida.writeUTF(resposta);
                        return; // finaliza essa thread, mas o servidor segue
                    default:
                        resposta = "Opção inválida.";
                        break;
                }

                saida.writeUTF(resposta);
            }

            socketCliente.close();

        } catch (Exception e) {
            System.out.println("Erro no cliente: " + e.getMessage());
        }
    }

    // Pega uma fortuna aleatória do arquivo
    public String selecionarFortuna() {
        ArrayList<String> fortunas = new ArrayList<>();

        try {
            BufferedReader leitor = new BufferedReader(new FileReader("src/fortune-br.txt"));
            String linha;
            StringBuilder fortuna = new StringBuilder();

            while ((linha = leitor.readLine()) != null) {
                if (linha.equals("%")) {
                    fortunas.add(fortuna.toString());
                    fortuna.setLength(0);
                } else {
                    fortuna.append(linha).append("\n");
                }
            }

            leitor.close();

        } catch (Exception e) {
            System.out.println("Erro na leitura do arquivo: " + e.getMessage());
        }

        if (fortunas.size() > 0) {
            Random random = new Random();
            return fortunas.get(random.nextInt(fortunas.size()));
        } else {
            return "Nenhuma fortuna encontrada.";
        }
    }

    // Adiciona uma nova fortuna ao arquivo
    public void adicionarFortuna(String argumento) {
        try {
            BufferedWriter fortuna = new BufferedWriter(new FileWriter("src/fortune-br.txt", true));
            fortuna.write(argumento);
            fortuna.newLine();
            fortuna.write("%");// O servidor escreve o %
            fortuna.newLine();
            fortuna.close();
        } catch (Exception e) {
            System.out.println("Erro ao escrever no arquivo: " + e.getMessage());
        }
    }

    public void parser(String mensagem) {
        this.metodo = "";
        this.argumento = "";

        if (mensagem.contains("\"method\":\"read\"")) {
            this.metodo = "read";

        } else if (mensagem.contains("\"method\":\"write\"")) {
            this.metodo = "write";
            int i1 = mensagem.indexOf("[\"") + 2;
            int i2 = mensagem.indexOf("\"]");
            this.argumento = mensagem.substring(i1, i2);

        } else if (mensagem.contains("\"method\":\"close\"")) {
            this.metodo = "close";
        }
    }

    public static void main(String[] args) {
        new Servidor().iniciar();
    }
}
