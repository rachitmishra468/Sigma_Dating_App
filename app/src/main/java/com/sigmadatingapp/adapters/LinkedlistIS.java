package com.sigmadatingapp.adapters;
// Java program to sort link list
// using insertion sort

public class LinkedlistIS {
    Node head;

   static class Node {
        int data;
        Node next;

        Node(int d) {
            data = d;
            next = null;

        }
    }

    public LinkedlistIS Insert(LinkedlistIS linkedlistIS, int data) {

        Node node = new Node(data);
        node.next = null;
        if (linkedlistIS.head == null) {
            linkedlistIS.head = node;
        } else {
            Node last = linkedlistIS.head;
            while (last.next != null) {
                last = last.next;

            }
            last.next = node;
        }


        return linkedlistIS;
    }

    public static void print(LinkedlistIS list) {
        Node currentnode = list.head;
        System.out.print("Linklist ");
        while (currentnode.next != null) {
            System.out.print(currentnode.data);
            currentnode = currentnode.next;
        }
    }


    public static void main(String args[]) {
        LinkedlistIS linkedlistIS = new LinkedlistIS();
        linkedlistIS.Insert(linkedlistIS, 5);
        linkedlistIS.Insert(linkedlistIS, 25);
        linkedlistIS.Insert(linkedlistIS, 85);
        linkedlistIS.Insert(linkedlistIS, 15);
        linkedlistIS.print(linkedlistIS);


    }

}

