package com.nhnacademy.tictactoe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Server {

    private static List<Socket> players = new ArrayList<>();

    private static int count = 0;

    public static synchronized void increase() {
        count++;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 1234;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException exception) {
                System.out.println("포트 번호는 정수만 입력해주세요.");
            }
        }

        try (ServerSocket server = new ServerSocket(port)) {
            while (count < 2) {
                Socket socket = server.accept();
                increase();
                players.add(socket);
            }
            TicTacToe tictactoe = new TicTacToe();
            tictactoe.start();
            tictactoe.join();
        }
    }

    static class TicTacToe extends Thread {
        private String[][] board = {{INIT, INIT, INIT},
                {INIT, INIT, INIT},
                {INIT, INIT, INIT}};

        private static final int ERROR = -1;
        private static final String INIT = " ";

        List<BufferedReader> readers = new ArrayList<>();
        List<BufferedWriter> writers = new ArrayList<>();

        private int currentPlayer = 0;
        private int numberOfTurn = 0;

        public TicTacToe() throws IOException {
            readers.add(new BufferedReader(new InputStreamReader(players.get(currentPlayer).getInputStream())));
            writers.add(new BufferedWriter(new OutputStreamWriter(players.get(currentPlayer).getOutputStream())));
            sendToPlayer(writers.get(currentPlayer), "id는 player" + (currentPlayer + 1) + "입니다.");
            nextUser();

            readers.add(new BufferedReader(new InputStreamReader(players.get(currentPlayer).getInputStream())));
            writers.add(new BufferedWriter(new OutputStreamWriter(players.get(currentPlayer).getOutputStream())));
            sendToPlayer(writers.get(currentPlayer), "id는 player" + (currentPlayer + 1) + "입니다.");
            nextUser();
        }

        @Override
        public void run() {
            String inputLine = " ";
            sendBoard();

            while (!Thread.currentThread().isInterrupted() && numberOfTurn < 9) {
                sendToAll("player" + (currentPlayer + 1) + "의 차례입니다.");
                sendToPlayer(writers.get(currentPlayer), "notify");
                sendToPlayer(writers.get(currentPlayer), "1~9까지의 값 중 하나를 입력해주세요.");
                try {
                    inputLine = readers.get(currentPlayer).readLine();
                } catch (IOException ignore) {
                }

                int inputInt = isValidInput(inputLine);
                if (inputInt == ERROR) {
                    continue;
                }

                int[] coordinate = converseToCoordinate(inputInt);
                int row = coordinate[0];
                int col = coordinate[1];

                if (isAlreadyPlaced(row, col)) {
                    continue;
                }

                sendBoard();
                putToBoard(row, col);
                sendToAll("player" + (currentPlayer + 1) + "가 놓은 후 :");
                sendBoard();


                if (gameEnd()) {
                    sendToAll("player" + (currentPlayer + 1) + "가 이겼습니다.");
                    sendToAll("exit");
                    return;
                }
                nextUser();
                numberOfTurn++;
            }

            sendToAll("무승부입니다.");
            sendToAll("exit");
        }

        public void sendToPlayer(BufferedWriter writer, String message) {
            try {
                writer.write(message.concat("\n"));
                writer.flush();
            } catch (IOException exception) {
                exception.printStackTrace();
                System.out.println("IOException, 비정상 종료");
                System.exit(1);
            }
        }

        public void sendToAll(String message) {
            for (int i = 0; i < players.size(); i++) {
                sendToPlayer(writers.get(i), message);
            }
        }

        public void sendBoard() {
            String currentBoard = makeBoard();
            sendToPlayer(writers.get(0), currentBoard);
            sendToPlayer(writers.get(1), currentBoard);
        }

        private String makeBoard() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    builder.append("[ ").append(board[i][j]).append(" ] ");
                }
                builder.append("\n");
            }
            return builder.toString();
        }

        public boolean gameEnd() {
            if (gameEndByRow()) {
                return true;
            } else if (gameEndByCol()) {
                return true;
            } else {
                return gameEndDiagonal();
            }
        }

        private boolean gameEndByRow() {
            for (int row = 0; row < 3; row++) {
                if (!board[row][0].equals(INIT) && board[row][0].equals(board[row][1]) &&
                        board[row][1].equals(board[row][2])) {
                    return true;
                }
            }
            return false;
        }

        private boolean gameEndByCol() {
            for (int col = 0; col < 3; col++) {
                if (!board[0][col].equals(INIT) && board[0][col].equals(board[1][col]) &&
                        board[1][col].equals(board[2][col])) {
                    return true;
                }
            }
            return false;
        }

        private boolean gameEndDiagonal() {
            if (!board[0][0].equals(INIT) && board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2])) {
                return true;
            } else {
                return !board[2][0].equals(INIT) && board[2][0].equals(board[1][1]) &&
                        board[1][1].equals(board[0][2]);
            }
        }

        private int isValidInput(String input) {
            int x = ERROR;

            if (Objects.isNull(input)) {
                return ERROR;
            }

            try {
                System.out.println("input : " + input);
                x = Integer.parseInt(input.trim());
            } catch (NumberFormatException exception) {
                sendToPlayer(writers.get(currentPlayer), "숫자를 입력하세요");
            }

            if (!isInRange(x)) {
                x = ERROR;
            }

            return x;
        }

        private int[] converseToCoordinate(int input) {
            switch (input) {
                case 1:
                    return new int[] {0, 0};
                case 2:
                    return new int[] {0, 1};
                case 3:
                    return new int[] {0, 2};
                case 4:
                    return new int[] {1, 0};
                case 5:
                    return new int[] {1, 1};
                case 6:
                    return new int[] {1, 2};
                case 7:
                    return new int[] {2, 0};
                case 8:
                    return new int[] {2, 1};
                default:
                    return new int[] {2, 2};
            }
        }

        private boolean isAlreadyPlaced(int row, int col) {
            if (!board[row][col].equals(INIT)) {
                sendToPlayer(writers.get(currentPlayer), "이미 놓여진 자리입니다.");
                return true;
            }
            return false;
        }

        private boolean isInRange(int input) {
            return input > 0 && input < 10;
        }

        private void putToBoard(int row, int col) {
            if (currentPlayer == 0) {
                board[row][col] = "O";
            } else {
                board[row][col] = "X";
            }
        }

        private synchronized void nextUser() {
            this.currentPlayer = (this.currentPlayer + 1) % players.size();
        }
    }
}
