package pojo;

import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Storage {
    //保证每个进程分配内存的时候都是互斥的
    public Object lockOfStore = new Object();
    //显示长度为需要储存空间的一半
    private static final int STORAGE_LENGTH = 600;
    //用来储存未被利用的表
    private List<Table> emptyTableList;
    //用来储存已被利用的表
    private List<Table> dominateTableList;

    public Storage() {
        Table table = new Table(0,600,0);
        emptyTableList = new ArrayList<>();
        dominateTableList = new ArrayList<>();
        emptyTableList.add(table);
    }
    public void InitStorage(){
        emptyTableList.clear();
        dominateTableList.clear();
        Table table = new Table(0,600,0);
        emptyTableList.add(table);
    }

    public List<Table> getEmptyTable() {
        return emptyTableList;
    }

    public List<Table> getDominateTable() {
        return dominateTableList;
    }
    //进程获取存储空间
    public Table getStorage(int roomOfStore){
        for (Table table : emptyTableList){
            if (table.getLength() >= roomOfStore){
                Table tableDomination = new Table(table.getBeginAddress() , roomOfStore , 1);
                dominateTableList.add(tableDomination);
                table.setBeginAddress(table.getBeginAddress() + roomOfStore);
                table.setLength(table.getLength() - roomOfStore);
                Collections.sort(emptyTableList);
                Collections.sort(dominateTableList);
                return tableDomination;
            }
        }
        return null;
    }
    public void ReleaseStorage(Table table){
        int value = 0;
        //在list列表中删除对应的table
        dominateTableList.remove(table);
        for (Table table1 : emptyTableList){
            if ((table1.getBeginAddress() + table1.getLength()) == table.getBeginAddress()){
                table1.setLength(table1.getLength() +  table.getLength());
                value = 1;
                break;
            }
            else if ((table.getBeginAddress() + table.getLength()) == table1.getBeginAddress()){
                table1.setBeginAddress(table.getBeginAddress());
                table1.setLength(table.getLength() + table1.getLength());
                value = 1;
                break;
            }
        }
        if (value == 0){
            emptyTableList.add(table);
        }
        Collections.sort(emptyTableList);
        Collections.sort(dominateTableList);
    }
}
