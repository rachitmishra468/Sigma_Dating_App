package com.SigmaDating.apk.adapters;

import java.util.Stack;

public class Test {


    String  s = "Hello";
    Stack put_value=new Stack();


    public  void reverse_str(String s){

        for(int i=0;i<s.length();i++){

            if(is_vovel(s.charAt(i))){
                put_value.push(s);
            }

        }

        make_string_again(s);



    }

    public void make_string_again(String s){
        for(int i=0;i<s.length();i++){

            if(is_vovel(s.charAt(i))){
               // put_value.push(s);
            }
            else {



            }

        }

    }


    public  boolean is_vovel(char c){
        char a[]={'a','e','i','o','u'};

        return  false;
    }

    public static void main(String args[]){

    }
}
