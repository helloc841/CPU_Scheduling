package pojo;

public class Pojo_PCB {
    //记录运行队列中的运行的线程
    private Thread myThread;
    //用于计数创建了几个进程
    static public int count = 0;
    //进程名
    private String processName;
    //要求运行时间
    private int runtime;
    //优先权
    private int priority;
    //状态：后备队列:1,就绪队列:2,运行队列:3,挂起:4,解挂:5
    private int statue;
    //指针
    public String point = "";
    private int roomOfStore;
    private int beginAddress;
    private int hungupNumber = 0;

    public int getHungupNumber() {
        return hungupNumber;
    }

    public void AddHungupNumber() {
        this.hungupNumber++;
    }
    public void setHungupNumber(int hungupNumber){
        this.hungupNumber = hungupNumber;
    }

    public int getRoomOfStore() {
        return roomOfStore;
    }

    public void setRoomOfStore(int roomOfStore) {
        this.roomOfStore = roomOfStore;
    }

    public int getBeginAddress() {
        return beginAddress;
    }

    public void setBeginAddress(int beginAddress) {
        this.beginAddress = beginAddress;
    }

    public Thread getMyThread() {
        return myThread;
    }

    public void setMyThread(Thread myThread) {
        this.myThread = myThread;
    }

    public Pojo_PCB(){
        count++;
        processName = "P"+count;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public static int getCount() {
        return count;
    }

    public static void setCount(int count) {
        Pojo_PCB.count = count;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getStatue() {
        return statue;
    }

    public void setStatue(int statue) {
        this.statue = statue;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "Pojo_PCB{" +
                "进程名='" + processName + '\'' +
                "\n 运行时间=" + runtime +
                "\n 优先权=" + priority +
                "\n 状态=" + this.getRealStatue() +
                "\n 指针=" + this.getPoint()+
                "\n 存储空间=" + this.getRoomOfStore() +
                '}';
    }

    public String getRealStatue(){
        switch (this.statue){
            case 1: return "后备队列";
            case 2: return "就绪队列";
            case 3: return "运行队列";
            case 4: return "挂起队列";
            default: return "解挂队列";
        }
    }
}
