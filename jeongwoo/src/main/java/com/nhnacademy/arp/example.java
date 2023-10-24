//package com.nhnacademy.arp;
//
//import java.util.ArrayList;
//import org.jnetpcap.Pcap;
//import org.jnetpcap.PcapIf;
//
//public class example {
//    public static void main(String[] args) {
//        ArrayList<PcapIf> allDevs = new ArrayList<PcapIf>();
//        StringBuilder errbuf = new StringBuilder();
//
//        int r = Pcap.findAllDevs(allDevs, errbuf);
//        if (r == Pcap.NOT_OK || allDevs.isEmpty()) {
//            System.out.println("네트워크 장치 못찾음. " + errbuf.toString());
//            return;
//        }
//
//        System.out.println("[네트워크 장비 탐색 성공]");
//        int i = 0;
//        for (PcapIf device : allDevs) {
//            String description = (device.getDescription() != null) ? device.getDescription() : "장비에 대한 설명이 없습니다.";
//            System.out.printf("[%d번]: %s [%s]", i++, device.getName(), description);
//        }
//    }
//}
