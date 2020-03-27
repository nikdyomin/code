package com.company;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Stack;

public class Main {

    public static void main(String[] args) throws IOException {
        TakeInfo ti = new TakeInfo();
        Tree<String> rootFolder = new Tree<>("RootFolder");
        ti.giveInfo(rootFolder);
        printTree(rootFolder);

    }

    private static <T> void printTree(Node<T> node) {
        printTree(node, 0);
    }

    private static <T> void printTree(Node<T> node, int level) {
        printNode(node, level);
        if (node.getChildren() != null) {
            for (Node<T> childNode : node.getChildren()) {
                printTree(childNode, level + 1);
            }
        }
    }

    private static <T> void printNode(Node<T> kid, int level) {

        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }

        System.out.println(kid.getData());
    }
}

//**************Описание структуры дерева***************//

class Tree<T> extends Node<T> {

    public Tree(T data) {

        super(data, null);
    }
}

class Node<T> {
    private T data;
    private final List<Node<T>> children = new ArrayList<>();
    private final Node<T> parent;

    public Node(T data, Node<T> parent) {
        this.data = data;
        this.parent = parent;
    }

    public void addChild(Node<T> node) {
        children.add(node);
    }

    public Node<T> addChild(T nodeData) {

        Node<T> newNode = new Node<T>(nodeData, this);
        children.add(newNode);
        return newNode;
    }


    public List<Node<T>> getChildren() {
        return children;
    }


    public T getData() {
        return data;
    }

}


class TakeInfo extends IOException {
    Stack<Integer> mStack = new Stack<>(); //Реализация стека для корректного отображения родителя узла
    ArrayList<Node<String>> nodesList = new ArrayList<>(); //Лист в котором хранятся узлы
    FileReader fr;
    String text = ""; //разбираемый текст
    int parentId = 0; //id родителя
    int nodeId = 0; //id узла
    int[] idR = new int[400]; //для корректного отображения родителя списка узлов
    int qR = 0; //для корректного отображения родителя списка узлов
    int check = 0; //для корректного отображения родителя списка узлов

    public void giveInfo(Node<String> rootFolder) {
        try {
            FileWriter fw = new FileWriter("out.txt");
            fr = new FileReader("in.txt");
            Scanner sc = new Scanner(fr);
            Pattern forNodesStart = Pattern.compile("[\\w-]+[ =]+[{]"); //Регулярное выражение для поиска типа: "узел = { "
            Pattern forNodesEnd = Pattern.compile(".*}.*"); //Регулярное выражение для поиска закрытия фигурной скобки
            Pattern valuePattern = Pattern.compile("“\\w+”"); //Регулярное выражение для значения узла, отдельно от имени
            Pattern forNodesNewField = Pattern.compile("\\w*.=.“\\w+”"); //Регулярное выражение для поиска "имя_узла = содержимое_узла"
            Pattern namePattern = Pattern.compile("\\w*[^\\s=+{]"); //Регулярное выражения для имени узла, отдельно от значения

            nodesList.add(0, rootFolder.addChild(""));
            mStack.push(0);
            while (sc.hasNextLine()) {
                text = sc.nextLine() + "\n";
                Matcher forNodeStart = forNodesStart.matcher(text);
                Matcher forNodeEnd = forNodesEnd.matcher(text);
                Matcher forNodeStartNewField = forNodesNewField.matcher(text);
                //Пока найдено "узел = { "
                while (forNodeStart.find()) {
                    String newObj = text.substring(forNodeStart.start(), forNodeStart.end());
                    Matcher forObjectName = namePattern.matcher(newObj);
                    nodeId++;
                    idR[qR] = nodeId;
                    check = idR[qR];
                    qR++;
                    //Добавления узла в ArrayList, запись в файл, добавление в стек
                    if (forObjectName.find()) {
                        String objectName = newObj.substring(forObjectName.start(), forObjectName.end());
                        nodesList.add(nodeId, nodesList.get(mStack.peek()).addChild(objectName));
                        fw.write(nodeId + ", " + mStack.peek() + ", " + objectName + "\n");
                        mStack.push(nodeId);
                        parentId = mStack.peek();
                    }
                }
                //Пока найдено "имя_узла = содержимое_узла"
                while (forNodeStartNewField.find()) {
                    String field = text.substring(forNodeStartNewField.start(), forNodeStartNewField.end());
                    Matcher forFieldName = namePattern.matcher(field);
                    Matcher forFieldValue = valuePattern.matcher(field);
                    qR++;
                    nodeId++;
                    //Для корректного отображения имени узла
                    if (forFieldName.find()) {
                        String fieldName = field.substring(forFieldName.start(), forFieldName.end());
                        //Для корректного отображения содержимого узла
                        if(forFieldValue.find()) {
                            String fieldValue= field.substring(forFieldValue.start(), forFieldValue.end());
                            nodesList.add(nodeId, nodesList.get(mStack.peek()).addChild(field));
                            fw.write(nodeId + ", " + parentId + ", " + fieldName +", "+ fieldValue+"\n");
                        }
                    }
                }
                //Пока найдена }
                while (forNodeEnd.find()) {
                    parentId--;
                    qR--;
                    check = idR[qR];
                    mStack.pop();
                }
            }
            fr.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
