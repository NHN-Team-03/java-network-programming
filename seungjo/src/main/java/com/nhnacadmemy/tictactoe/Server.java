package com.nhnacadmemy.tictactoe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    static Map<String, Socket> players = new HashMap<>();

    static int gameCount = 0;
    static String[][] board = new String[3][3];


    // TODO: 1개의 핸들러로 2개의 Player를 처리하도록 수정
    static class PlayerHandler extends Thread {

        List<BufferedReader> readerList = new ArrayList<>();
        List<BufferedWriter> writerList = new ArrayList<>();

        public PlayerHandler() {
            boardInit();
        }

        public void addPlayer(Socket socket) {
            try {
                readerList.add(new BufferedReader(new InputStreamReader(socket.getInputStream())));
                writerList.add(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            } catch (IOException ignore) {
            }
        }

        private void boardInit() {
            for (String[] strings : board) {
                Arrays.fill(strings, " ");
            }
        }


        private void closeConnection() {
            try {
                for (int i = 0; i < 2; i++) {
                    readerList.get(i).close();
                    writerList.get(i).close();
                }
                for (Socket socket : players.values()) {
                    socket.close();
                }
            } catch (IOException ignore) {
            }
        }


        private void sendMessage(int playerIndex, String message) {
            try {
                writerList.get(playerIndex).write(message);
                writerList.get(playerIndex).newLine();
                writerList.get(playerIndex).flush();
            } catch (IOException ignore) {
            }
        }

        private void printBoard() {
            StringBuilder sb = new StringBuilder();
            sb.append("============\n");
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    sb.append("[").append(board[i][j]).append("] ");
                }
                sb.append("\n");
            }

            sendMessage(0, sb.toString());
            sendMessage(1, sb.toString());
        }

        private boolean gameOver() {
            return getWinner() != null || isBoardFull();
        }

        private boolean isBoardFull() {
            for (String[] row : board) {
                for (String cell : row) {
                    if (cell.equals(" ")) {
                        return false;
                    }
                }
            }
            return true;
        }

        private String getWinner() {


            // 가로와 세로 체크
            for (int i = 0; i < board.length; i++) {
                if ((board[i][0].equals(board[i][1]) && board[i][1].equals(board[i][2])) && !board[i][0].equals(" ")) {
                    return board[i][0];
                }
                if ((board[0][i].equals(board[1][i]) && board[1][i].equals(board[2][i])) && !board[0][i].equals(" ")) {
                    return board[0][i];
                }
            }

            // 대각선 체크
            if ((board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2])) && !board[0][0].equals(" ")) {
                return board[0][0];
            }
            if ((board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0])) && !board[0][2].equals(" ")) {
                return board[0][2];
            }

            if (isBoardFull()) {
                return "draw";
            }

            return null;

        }

        @Override
        public void run() {
            try {
                while (!gameOver()) {

                    int currentPlayerIndex = gameCount % 2;
                    int otherPlayerIndex = (currentPlayerIndex + 1) % 2;

                    printBoard();
                    sendMessage(currentPlayerIndex, "좌표(1 ~ 9)를 입력하세요");
                    sendMessage(otherPlayerIndex, "상대방이 입력중입니다.");

                    String input = readerList.get(currentPlayerIndex).readLine();

                    if (input.matches("[1-9]")) {
                        int position = Integer.parseInt(input) - 1;
                        int x = position / 3;
                        int y = position % 3;


                        if (board[x][y].equals(" ")) {
                            board[x][y] = currentPlayerIndex == 0 ? "O" : "X";
                            gameCount++;
                        } else {
                            sendMessage(currentPlayerIndex, "그 자리는 비어있지 않습니다.");
                            continue;
                        }
                    } else {
                        sendMessage(currentPlayerIndex, "1 ~ 9 사이의 숫자를 입력해주세요.");
                        continue;
                    }
                }

                printBoard();
                String winner = getWinner() == "O" ? players.keySet().toArray()[0].toString() :
                        players.keySet().toArray()[1].toString();
                sendMessage(0, "Game Over! " + winner + " win!");
                sendMessage(1, "Game Over! " + winner + " win!");

            } catch (IOException ignore) {
            } finally {
                closeConnection();
            }
        }


    }


    public static void main(String[] args) {
        int port = 1234;


        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignore) {
                System.out.println("Port: 1 ~ 65535");
                System.exit(1);
            }
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            PlayerHandler handler = new PlayerHandler();
            for (int i = 0; i < 2; i++) {
                Socket socket = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String id = reader.readLine();
                players.put(id, socket);
                handler.addPlayer(socket);
                System.out.println(id + "님이 접속하였습니다.");
            }
            handler.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}