package com.drownedinsound;

public class GenerateModel {


    public static void main(String[] args) {
        try {
            new DisBoardsDaoGenerator().generateModel();
        } catch (Exception e) {
            System.out.println("MapiDaoGenerator Exception [" + e + "]");
        }

    }

}
