package org.javxtend;

import org.javxtend.util.*;

public class Main {
    public static void main(String[] args) {
        var data = JXTriple.of("Zdenek", 29, true);
        var dataList = data.toList();
        System.out.println(dataList);

    }
}