package pojo;

public class Table implements Comparable<Table>{
    private int beginAddress;
    private int length;
    //0为未分状态 1为占有状态
    private int state;
    public Table(){}
    public Table(int beginAddress){
        this.beginAddress = beginAddress;
    }

    public Table(int beginAddress, int length, int state) {
        this.beginAddress = beginAddress;
        this.length = length;
        this.state = state;
    }

    public int getBeginAddress() {
        return beginAddress;
    }

    public void setBeginAddress(int beginAddress) {
        this.beginAddress = beginAddress;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public int compareTo(Table o) {
        if (this.getBeginAddress() <= o.getBeginAddress())
            return -1;
        else
            return 1;
    }
}
