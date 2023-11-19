package pojo;

public class ProcessManege {
    public Object lock = new Object();
    public int goodFlag = 1;
    public int statue;
    static int count = 5;
    private int out;
    public Pojo_PCB WaitPojoPCB;
    //创建50个进程
    Pojo_PCB []PCBList;

    public Pojo_PCB[] getPCBList() {
        return PCBList;
    }

    public void setPCBList(Pojo_PCB[] PCBList) {
        this.PCBList = PCBList;
    }

    public int getOut() {
        return out;
    }

    public void setOut(int out) {
        this.out = out;
    }

    public ProcessManege(int statue){
        //创建50个进程
        PCBList = new Pojo_PCB[5];
        this.statue = statue;
        out = 0;
    }
    public void addProcess(Pojo_PCB pojo_pcb){
            PCBList[out] = pojo_pcb;
            out++;
            RankProcess();
    }
    public void deleteProcess(Pojo_PCB pojoPcb){
        for (int i = 0 ; i < out ; i++){
            if (PCBList[i].getProcessName().equals(pojoPcb.getProcessName())){
                //删除该PCB
                for (int j = i ; j < out - 1 ; j++)
                    PCBList[j] = PCBList[j+1];
                out--;
                PCBList[out] = null;
                break;
            }
        }
    }
    //查找进程
    public Pojo_PCB FindProcess(String ProcessName){
        for (int i = 0 ; i < out ; i++){
            if (PCBList[i].getProcessName().equals(ProcessName))
                return PCBList[i];
        }
        return null;
    }
    //返回优先权第一的进程 并删除第一个进程
    public Pojo_PCB ReturnFirstProcess(){
        Pojo_PCB pojo_pcb = PCBList[0];
        deleteProcess(PCBList[0]);
        return pojo_pcb;
    }
    //为进程根据优先权排序
    public void RankProcess(){
        boolean swap = false;
        for (int i = 0 ; i < out ; i++){
            for (int j = 0 ; j < out -  i - 1 ; j++){
                if (PCBList[j].getPriority() == PCBList[j + 1].getPriority() && PCBList[j].getRuntime() > PCBList[j + 1].getRuntime()){
                    Pojo_PCB pojo_pcb = PCBList[j];
                    PCBList[j] = PCBList[j + 1];
                    PCBList[j + 1] = pojo_pcb;
                    swap = true;
                }
                else if (PCBList[j].getPriority() > PCBList[j + 1].getPriority()){
                    Pojo_PCB pojo_pcb = PCBList[j];
                    PCBList[j] = PCBList[j + 1];
                    PCBList[j + 1] = pojo_pcb;
                    swap = true;
                }
            }
            if (!swap)
                break;
        }
        for (int i = 0 ; i < out - 1 ; i++){
            PCBList[i].setPoint(PCBList[i+1].getProcessName());
        }
        PCBList[out - 1].setPoint("null");
    }
    public boolean IsEmpty(){
        if (this.out == 5){
            return false;
        }
        return true;
    }
    public boolean IsElement(){
        if (this.out != 0){
            return true;
        }
        return false;
    }
    public void removeAllProcess(){
        for (int i = 0 ; i < out ; i++){
            PCBList[i] = null;
        }
        out = 0;
    }
}
