package pojo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Text {
    public static void main(String[] args) {
        List<Table> textTable = new ArrayList<>();
        textTable.add(new Table(10));
        textTable.add(new Table(9));
        textTable.add(new Table(8));
        textTable.add(new Table(7));
        textTable.add(new Table(6));
        textTable.add(new Table(5));
        textTable.add(new Table(4));
        textTable.add(new Table(3));
        Collections.sort(textTable);
        for (Table table : textTable){
            System.out.println(table.getBeginAddress());
        }
    }
}
