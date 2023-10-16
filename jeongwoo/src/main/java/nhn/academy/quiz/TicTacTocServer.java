package nhn.academy.quiz;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class TicTacTocServer {
    // Server에서 누구 차례인지 전송
    // 보드 관리

    private static Map<String, PlayerHandler> playerHandlerMap = new HashMap<>();
    private static int boardSize = 3;
    private static char board[][] = {{' ', ' ', ' ',},
            {' ', ' ', ' ',},
            {' ', ' ', ' ',},
    };

    private static boolean[] checkBlank = new boolean[9];
    private static char[] shape = {'O', 'X'};
    private static int count = 0;

    public static void main(String[] args) {
        int port = 1234;

        try {
            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            }
        } catch (NumberFormatException ignore) {
            System.out.println("0 ~ 65535 크기의 정수만 입력가능");
            System.exit(1);
        }

        try {
            ServerSocket serversocket = new ServerSocket(port);
            boolean turn = true;
            while (count < 2) {
                Socket socket = serversocket.accept();
                PlayerHandler handler = new PlayerHandler(socket, turn, shape[count]);
                handler.start();
                turn = !turn;
                count++;

            }

        } catch (IOException ignore) {
            System.out.println("서비스 열기 실패");
        }
    }

    static class PlayerHandler extends Thread {
        private char winnderShape = ' ';
        private Socket socket;
        private int bufferSize = 2048;
        private String ID;

        private boolean myTurn;
        private char shape;
        BufferedOutputStream output;

        public String getID() {
            return ID;
        }

        public PlayerHandler(Socket socket, boolean myTurn, char shape) {
            this.shape = shape;
            this.myTurn = myTurn;
            this.socket = socket;
            System.out.println(socket.getInetAddress().getHostAddress() + " : " + socket.getPort() + "가 연결");
        }

        public void setMyTurn(boolean myTurn) {
            this.myTurn = myTurn;
        }

        public void changeTurn(){
            for (String id : playerHandlerMap.keySet()) {
                if(id.equals(this.getID())){
                    this.myTurn = false;
                    continue;
                }
                playerHandlerMap.get(id).setMyTurn(true);
            }
        }

        public void send(String id,String data){
            playerHandlerMap.get(id).receive(data);
        }

        public void sendAll(String data) {
            for (String id : playerHandlerMap.keySet()) {
                playerHandlerMap.get(id).receive(data);
            }
        }

        public void receive(String data) {
            try {
                output.write(data.getBytes());
                output.write("\n".getBytes());
                output.flush();
            } catch (IOException ignore) {

            }
        }

        public String drawPoint(String input) {
            try {
                int index = Integer.parseInt(input) - 1;
                if (checkBlank[index]) {
                    throw new IllegalArgumentException();
                }
                board[index / 3][index % 3] = shape;
                checkBlank[index] = true;

            } catch (NumberFormatException ignore) {
                return "입력 문자 오류";
            } catch (IllegalArgumentException ignore) {
                return "이미 있는 위치";
            }
            return "반영 되었습니다.";
        }


        private boolean whoWinner() { // 승리 체크

            for (int i = 0; i < 3; i++) {
                if (((board[i][0] == board[i][1] && board[i][1] == board[i][2])
                        || (board[0][i] == board[1][i] && board[1][i] == board[2][i])) && board[i][i] != ' ') {
                    winnderShape = board[i][i];
                    return true;
                }
            }

            if (((board[0][0] == board[1][1] && board[1][1] == board[2][2])
                    || (board[0][2] == board[1][1] && board[1][1] == board[2][0])) && board[1][1] != ' ') {
                winnderShape = board[1][1];
                return true;
            }
            return false;
        }
        @Override
        public void run() {
            try {

                output = new BufferedOutputStream(socket.getOutputStream());
                BufferedInputStream input = new BufferedInputStream(socket.getInputStream());

                byte[] buffer = new byte[bufferSize];
                String message;
                int length;

                length = input.read(buffer);
                ID = new String(buffer, 0, length);

                playerHandlerMap.put(ID, this);
                while ((length = input.read(buffer)) > 0) {
                    System.out.println(count);
                    if (count < 2) {
                        sendAll("인원이 아직 모이지 않았습니다.!!");
                        continue;
                    }

                    if(!myTurn){
                        send(getID(),"당신의 차례가 아닙니다!!");
                        continue;
                    }
                    message = new String(buffer, 0, length);
                    System.out.println(message);
                    output.write(drawPoint(message).getBytes());
                    output.write("\n".getBytes());
                    printBoardAll();
                    if(whoWinner()){
                        sendEndMessage();
                        playerHandlerMap.remove(getID());
                        count--;
                        break;
                    }
                    changeTurn();
                    output.flush();
                }
            } catch (IOException ignore) {
                System.out.println("서비스 오류");
            }
        }

        public void sendEndMessage(){
            for(String id : playerHandlerMap.keySet()){
                if(getWinnderShape() == playerHandlerMap.get(id).getShape()){
                    playerHandlerMap.get(id).receive("승리하셨습니다!!");
                    playerHandlerMap.get(id).receive("end");
                }
                else{
                    playerHandlerMap.get(id).receive("패배하셨습니다...");
                    playerHandlerMap.get(id).receive("end");
                }
            }
        }

        public char getShape() {
            return shape;
        }

        public char getWinnderShape() {
            return winnderShape;
        }

        private void printBoardAll() {
            for (String id : playerHandlerMap.keySet()) {
                playerHandlerMap.get(id).printBoard();
            }
        }

        private void printBoard() {
            try {
                for (int i = 0; i < boardSize; i++) {
                    for (int j = 0; j < boardSize; j++) {
                        if (j == 2) {
                            output.write(board[i][j]);
                            continue;
                        }
                        output.write((board[i][j] + "|").getBytes());
                    }
                    output.write("\n".getBytes());
                    if (i < 2) {
                        output.write("------\n".getBytes());
                    }
                }
                output.flush();
            } catch (IOException ignore) {

            }
        }
    }
}
