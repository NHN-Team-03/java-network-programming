package quiz.tictactoe;

import java.io.IOException;
import java.net.Socket;

public class Tictactoe {
    private String[][] board;
    private Socket socket1, socket2;
    private int count;

    public Tictactoe(Socket socket1, Socket socket2) {
        board = new String[3][3];
        this.socket1 = socket1;
        this.socket2 = socket2;
        this.count = 0;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = " ";
            }
        }
    }

    public void writeBoard(int x, int y, String suite) {
        board[x][y] = suite;
    }

    private boolean winnerCheck(String suite) {
        for (int i = 0; i < board.length; i++) {
            if (board[i][0].equals(suite) && board[i][0].equals(board[i][1]) && board[i][0].equals(board[i][2])) {
                return true;
            } else if (board[0][i].equals(suite) && board[0][i].equals(board[1][i]) &&
                    board[0][i].equals(board[2][i])) {
                return true;
            }
        }

        if (board[1][1].equals(suite) && (board[1][1].equals(board[0][0]) && board[1][1].equals(board[2][2]) ||
                board[1][1].equals(board[2][0]) && board[1][1].equals(board[0][2]))) {
            return true;
        }

        return false;
    }

    public void readyToGame() {
        try {
            socket1.getOutputStream().write("게임 준비가 완료되었습니다. 게임을 시작합니다.\n".getBytes());
            socket2.getOutputStream().write("게임 준비가 완료되었습니다. 게임을 시작합니다.\n".getBytes());
        } catch (IOException ignore) {
        }
    }

    public void start() {
        readyToGame();

        while (true) {
            printBoard(socket1);
            printBoard(socket2);
            if (count % 2 == 0) {
                wait(socket2);
                playerTurn(socket1, "O", socket2);
            } else {
                wait(socket1);
                playerTurn(socket2, "X", socket1);
            }
            count++;
        }
    }

    public void end(Socket socket) {
        try {
            socket.getOutputStream().write("end\n".getBytes());
            socket.getOutputStream().flush();
        } catch (IOException ignore) {
        }
    }

    private void wait(Socket socket) {
        try {
            socket.getOutputStream().write("상대방이 게임을 진행중입니다...\n".getBytes());
            socket.getOutputStream().flush();
        } catch (IOException ignore) {
        }
    }

    private void playerTurn(Socket socket, String suite, Socket loser) {
        try {
            while (true) {
                socket.getOutputStream().write("1~9의 칸 중에서 입력해주세요.\n".getBytes());
                socket.getOutputStream().flush();

                int length;
                byte[] buffer = new byte[2048];

                length = socket.getInputStream().read(buffer);

                String line = new String(buffer, 0, length);

                int space;
                try {
                    space = Integer.parseInt(line.trim()) - 1;

                    if (space > 8 || space < 0) {
                        socket.getOutputStream().write("범위를 벗어난 숫자입니다. 다시 입력해 주세요.\n".getBytes());
                        continue;
                    }
                    if (!board[space / 3][space % 3].equals(" ")) {
                        socket.getOutputStream().write("해당 칸에 둘 수 없습니다. 다시 입력해 주세요.\n".getBytes());
                        continue;
                    }

                    writeBoard(space / 3, space % 3, suite);
                    break;
                } catch (NumberFormatException e) {
                    socket.getOutputStream().write("잘못된 숫자를 입력했습니다. 다시 입력해 주세요.\n".getBytes());
                }
            }

            if (winnerCheck(suite)) {
                printBoard(socket1);
                printBoard(socket2);
                socket.getOutputStream().write("승리했습니다.\n".getBytes());
                socket.getOutputStream().flush();

                loser.getOutputStream().write("패배했습니다.\n".getBytes());
                loser.getOutputStream().flush();

                end(socket1);
                end(socket2);

                socket1.close();
                socket2.close();
            }
        } catch (IOException ignore) {
        }

    }

    private void printBoard(Socket socket) {
        try {
            socket.getOutputStream().write("================\n".getBytes());
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board.length; j++) {
                    socket.getOutputStream().write(("[" + board[i][j] + "]").getBytes());
                }
                socket.getOutputStream().write("\n".getBytes());
            }
            socket.getOutputStream().write("================\n".getBytes());
            socket.getOutputStream().flush();
        } catch (IOException ignore) {
        }
    }


}
