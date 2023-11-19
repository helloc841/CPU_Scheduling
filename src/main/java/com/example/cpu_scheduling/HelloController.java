package com.example.cpu_scheduling;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import pojo.ProcessManege;
import pojo.Pojo_PCB;
import Service.Service;
import pojo.Storage;
import pojo.Table;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloController implements Initializable {
    // 创建一个可缓存的线程池
    ExecutorService executor = Executors.newCachedThreadPool();
    Object LockOfHungdown = new Object();
    //如果挂起队列有挂起进程 那么backupPassreadyThread就需要wait
    // 等待挂起队列中的进程被加入就绪队列之后才能继续从后备队列中取进程
    Object LockOfHungdownAndBackup = new Object();
    //判断挂起队列中是否有元素
    boolean IsElementOfHungDownProcessManage = false;
    //判断是否点击Label
    int LabelClickedNumber = -1;
    //判断是不是已经到达readyPassrunthread的Wait
    boolean isComingWait = false;
    //判断从run队列中退出来的进程有几个
    int hungDown = 0;
    //记录是否进入对同一缓冲池的互斥操作状态
    int count = 0;
    //记录是否进行删除了ready队列中的第一个进程
    boolean IsDeleteReady = false;
    //用于锁住没有run中退出来的进程时的 readyPassrunThread
    Object lock = new Object();
    //保证每个run进程的互斥
    Object lockOfRun = new Object();
    //保证先删除了ready队列中的第一个进程 再添加回来的进程到run中
    Object lockOfDelete = new Object();
    //定义一个进程管理系统
    ProcessManege backupprocessmanege;
    ProcessManege readyprocessmanege;
    ProcessManege runprocessmanege;
    ProcessManege hungprocessmanege;
    ProcessManege hungdownprocessmanage;
    PassThread backupThread;
    PassThread ReadyThread;
    readyPassrunThread  readypassrunthread;
    HungDownThread hungDownThread;
    @FXML
    private Label backup1;
    @FXML
    private Label backup2;
    @FXML
    private Label backup3;
    @FXML
    private Label backup4;
    @FXML
    private Label backup5;
    @FXML
    private Label ready1;
    @FXML
    private Label ready2;
    @FXML
    private Label ready3;
    @FXML
    private Label ready4;
    @FXML
    private Label ready5;
    @FXML
    private Label run1;
    @FXML
    private Label run2;
    @FXML
    private Label run3;
    @FXML
    private Label run4;
    @FXML
    private Label run5;
    private Label[] labels;
    @FXML
    private Label hungup1;
    @FXML
    private Label hungup2;
    @FXML
    private Label hungup3;
    @FXML
    private Label hungup4;
    @FXML
    private Label hungup5;
    @FXML
    private Label hungdown1;
    @FXML
    private Label hungdown2;
    @FXML
    private Label hungdown3;
    @FXML
    private Label hungdown4;
    @FXML
    private Label hungdown5;
    @FXML
    private Label labeltip1;
    @FXML
    private Label labeltip2;
    @FXML
    private Button addbutton;
    @FXML
    private Pane storagePane;
    private Storage storage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //初始化进程管理
        backupprocessmanege = new ProcessManege(1);
        readyprocessmanege = new ProcessManege(2);
        runprocessmanege = new ProcessManege(3);
        hungprocessmanege = new ProcessManege(4);
        hungdownprocessmanage = new ProcessManege(5);
        storage = new Storage();
        backupThread = new PassThread(backupprocessmanege,readyprocessmanege,2);
        backupThread.start();
        ReadyThread = new PassThread(readyprocessmanege,runprocessmanege,3);
        ReadyThread.start();
        readypassrunthread = new readyPassrunThread();
        readypassrunthread.start();
        hungDownThread = new HungDownThread();
        hungDownThread.start();

        labels = new Label[25];
        addLabelToLabels();

        //添加事件
        addbutton.setOnAction(this::btnAddProcessOnClicked);
        for (int i = 0 ; i < 25 ; i++){
            labels[i].setOnMouseEntered(this::labelSetMouseEntered);
            labels[i].setOnMouseExited(this::labelSetMouseLeaved);
        }
        for (int i = 5 ; i < 15 ; i++){
            labels[i].setOnMouseClicked(this::LabelHungProcessOnClicked);
        }
        for (int i = 15 ; i < 20 ; i++){
            labels[i].setOnMouseClicked(this::LabelHungDownProcessOnClicked);
        }
    }
    public void addLabelToLabels(){
        labels[0] = backup1;
        labels[1] = backup2;
        labels[2] = backup3;
        labels[3] = backup4;
        labels[4] = backup5;
        labels[5] = ready1;
        labels[6] = ready2;
        labels[7] = ready3;
        labels[8] = ready4;
        labels[9] = ready5;
        labels[10] = run1;
        labels[11] = run2;
        labels[12] = run3;
        labels[13] = run4;
        labels[14] = run5;
        labels[15] = hungup1;
        labels[16] = hungup2;
        labels[17] = hungup3;
        labels[18] = hungup4;
        labels[19] = hungup5;
        labels[20] = hungdown1;
        labels[21] = hungdown2;
        labels[22] = hungdown3;
        labels[23] = hungdown4;
        labels[24] = hungdown5;
    }
    public void DrawLabels(ProcessManege processManege){
        Pojo_PCB[] PCBList = processManege.getPCBList();
        Pojo_PCB pojo_pcb;
        int number;
        int statue = processManege.statue;
        for(int i = 0 ; i < 5 ; i++){
            pojo_pcb = PCBList[i];
            number = i + (statue - 1) * 5;
            if (i >= processManege.getOut()){
                labels[number].setText("");
            }
            else
            labels[number].setText(pojo_pcb.getProcessName());
        }
    }
    //点击添加进程
    private void btnAddProcessOnClicked(ActionEvent actionEvent){
        if (!(backupprocessmanege.IsEmpty())){
            labeltip1.setText("后备队列已满，不能再继续加入");
        }
        else {
            labeltip1.setText("");
            Pojo_PCB pojo_pcb = new Pojo_PCB();
            Service.setRuntime(pojo_pcb);
            Service.setPriority(pojo_pcb);
            Service.setStorage(pojo_pcb);
            pojo_pcb.setStatue(1);
            synchronized (backupprocessmanege.lock){
                if (backupprocessmanege.goodFlag == 0) {
                    try {
                        backupprocessmanege.lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                backupprocessmanege.goodFlag = 0;
                //把进程加入进程管理
                backupprocessmanege.addProcess(pojo_pcb);
                backupprocessmanege.goodFlag = 1;
                if (readyprocessmanege.IsEmpty()){
                    backupprocessmanege.lock.notifyAll();
                }
            }
            //重新设置Label
                DrawLabels(backupprocessmanege);
        }
    }
    private void LabelHungProcessOnClicked(MouseEvent mouseEvent){
        Label labelOfMouseClicked = (Label) mouseEvent.getSource();
        String ContentLabel = labelOfMouseClicked.getText();
        if (ContentLabel.equals("")){
            labeltip1.setText("此处无进程，无法挂起！");
        } else {
            //获取名字的最后一个数字
            int number = Integer.parseInt(ContentLabel.substring(ContentLabel.length() - 1));
            //如果不等于number的话就代表之前点击的是别的label要重新再点击一次
            if(LabelClickedNumber != number){
                labelOfMouseClicked.setStyle("-fx-background-color: white");
                LabelClickedNumber = number;
            }
            else if (LabelClickedNumber == number && !hungprocessmanege.IsEmpty()){
                labeltip1.setText("挂起队列已满，无法在进行挂起操作!");
            }
            else if (LabelClickedNumber == number){
                //更改number
                LabelClickedNumber = -1;
                //获取Pojo_pcb
                Pojo_PCB pojo_pcb;
                String IdLabel = labelOfMouseClicked.getId();
                String subString = IdLabel.substring(0,IdLabel.length() - 1);
                switch (subString){
                    case "ready" : pojo_pcb = readyprocessmanege.FindProcess(ContentLabel);
                        break;
                    default : pojo_pcb = runprocessmanege.FindProcess(ContentLabel);
                }
                if (pojo_pcb.getStatue() == 3){
                    if (pojo_pcb.getMyThread() == null) {
                        labeltip1.setText("进程已运行结束,挂起失败！");
                    }else {
                        pojo_pcb.getMyThread().interrupt();
                        //更改ui
                        labelOfMouseClicked.setStyle("-fx-background-color:  rgb(255,192,203)");
                        //删除run中的对应进程
                        ConsumerOfProcessManage(runprocessmanege,pojo_pcb);
                        //把这个进程加入到hungup中去
                        ProduceProcessManage(hungprocessmanege,pojo_pcb);
                        if (runprocessmanege.getOut() == 4){
                            ProcessManageNotifyAll(runprocessmanege);
                        }
                    }
                }
                else {
                    //更改ui
                    labelOfMouseClicked.setStyle("-fx-background-color:  rgb(204,232,207)");
                    //删除ready中的对应进程
                    ConsumerOfProcessManage(readyprocessmanege,pojo_pcb);
                    //加入hungup中
                    ProduceProcessManage(hungprocessmanege,pojo_pcb);
                    //此时准备队列已经被挂起了 发送信息给挂起队列或者准备队列让它们进来一个进程
                    ProcessManageNotifyAll(readyprocessmanege);
                }
                DrawLabels(readyprocessmanege);
                DrawLabels(runprocessmanege);
                DrawLabels(hungprocessmanege);
            }
        }
    }
    private void LabelHungDownProcessOnClicked(MouseEvent mouseEvent){
        Label labelOfMouseClicked = (Label) mouseEvent.getSource();
        String ContentLabel = labelOfMouseClicked.getText();

        if (ContentLabel.equals("")){
            labeltip1.setText("此处无挂起进程，无法进行解挂操作!");
        }
        else {
            //获取名字的最后一个数字
            int number = Integer.parseInt(ContentLabel.substring(ContentLabel.length() - 1));
            //如果不等于number的话就代表之前点击的是别的label要重新再点击一次
            if(LabelClickedNumber != number){
                labelOfMouseClicked.setStyle("-fx-background-color: white");
                LabelClickedNumber = number;
            }
            else if (LabelClickedNumber == number && !hungdownprocessmanage.IsEmpty()){
                labeltip1.setText("解挂队列已满，无法再进行解挂操作!");
            }
            else if(LabelClickedNumber == number) {
                //更改ui
                LabelClickedNumber = -1;
                labelOfMouseClicked.setStyle("-fx-background-color:  rgb(255,255,204)");

                //获取Pojo_pcb
                Pojo_PCB pojo_pcb = hungprocessmanege.FindProcess(ContentLabel);
                pojo_pcb.setHungupNumber(0);
                //挂起队列的消费者
                ConsumerOfProcessManage(hungprocessmanege,pojo_pcb);
                //解挂队列的生产者
                ProduceProcessManage(hungdownprocessmanage,pojo_pcb);
                ProcessManageNotifyAll(hungdownprocessmanage);
                DrawLabels(hungprocessmanege);
                DrawLabels(hungdownprocessmanage);
            }
        }
    }
    private void labelSetMouseEntered(MouseEvent mouseEvent){
        Label labelOfMe = (Label) mouseEvent.getSource();
        String ContentOfLabel = labelOfMe.getText();
        if (ContentOfLabel.equals("")){
            labeltip2.setText("无进程");
        }
        else {
            Pojo_PCB pojo_pcb;
            String IdLabel = labelOfMe.getId();
            String subString = IdLabel.substring(0,IdLabel.length() - 1);
            switch (subString){
                case "backup" : pojo_pcb = backupprocessmanege.FindProcess(ContentOfLabel);
                break;
                case "ready" : pojo_pcb = readyprocessmanege.FindProcess(ContentOfLabel);
                    break;
                case "run" : pojo_pcb = runprocessmanege.FindProcess(ContentOfLabel);
                    break;
                case "hungup" : pojo_pcb = hungprocessmanege.FindProcess(ContentOfLabel);
                break;
                default : pojo_pcb = hungdownprocessmanage.FindProcess(ContentOfLabel);
            }
            labeltip2.setText(pojo_pcb.toString());
        }
    }
    private void labelSetMouseLeaved(MouseEvent mouseEvent){
        labeltip2.setText("");
    }
    public void ProcessManageWait(ProcessManege processManege){
        synchronized (processManege.lock){
            try {
                processManege.lock.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void ProcessManageNotifyAll(ProcessManege processManege){
        synchronized (processManege.lock){
            processManege.lock.notifyAll();
        }
    }
    public void LockWait(Object mylock){
        synchronized (mylock){
            try {
                mylock.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void LockNotifyAll(Object mylock){
        synchronized (mylock){
            mylock.notifyAll();
        }
    }
    public Pojo_PCB ConsumerOfProcessManage(ProcessManege processManege,Pojo_PCB pojoPcb){
        Pojo_PCB pojo_pcb = null;
        if (processManege.goodFlag == 0) {
            count = 1;
            ProcessManageWait(processManege);
        }
        processManege.goodFlag = 0;
        if (pojoPcb == null)
        pojo_pcb = processManege.ReturnFirstProcess();
        else
            processManege.deleteProcess(pojoPcb);
        processManege.goodFlag = 1;
        if (count == 1){
            ProcessManageNotifyAll(processManege);
            count = 0;
        }
        return pojo_pcb;
    }
    public void ProduceProcessManage(ProcessManege processManege , Pojo_PCB pojo_pcb){
        if (processManege.goodFlag == 0) {
            count = 1;
            ProcessManageWait(processManege);
        }
        processManege.goodFlag = 0;
        //生产操作
        pojo_pcb.setStatue(processManege.statue);
        processManege.addProcess(pojo_pcb);
        processManege.goodFlag = 1;
        if (count == 1){
            count = 0;
            ProcessManageNotifyAll(processManege);
        }
    }
    class PassThread extends Thread {
        int ReceiveStatue;
        ProcessManege SendProcessManage;
        ProcessManege ReceiveProcessManage;
        public PassThread(ProcessManege SendProcessManage,ProcessManege ReceiveProcessManage,int ReceiveStatue){
            this.SendProcessManage = SendProcessManage;
            this.ReceiveProcessManage = ReceiveProcessManage;
            this.ReceiveStatue = ReceiveStatue;
        }
        @Override
        public void run() {
            while (true){
                //当挂起队列中有队列的时候那么这个时候后备队列就要wait等待挂起队列中的进程加入准备队列之后再从后备队列中取进程
                while (ReceiveStatue == 2 && IsElementOfHungDownProcessManage){
                    LockNotifyAll(LockOfHungdown);
                    hungDown = 0;
                    LockWait(LockOfHungdownAndBackup);
                }
                if (ReceiveStatue == 2)
                    hungDown = 1;
                //消费者部分
                while (!SendProcessManage.IsElement()){
                    //如果被消费的缓冲池为空 那么就陷入wait
                    ProcessManageWait(SendProcessManage);
                }
                if (ReceiveProcessManage.IsEmpty()){
                    //消费者部分
                    if (SendProcessManage.goodFlag == 0){
                        ProcessManageWait(SendProcessManage);
                    }
                    SendProcessManage.goodFlag = 0;
                    //消费
                    Pojo_PCB pojo_pcb = SendProcessManage.ReturnFirstProcess();
                    SendProcessManage.goodFlag = 1;
                    ProcessManageNotifyAll(SendProcessManage);

                    //生产者部分
                    if (ReceiveProcessManage.goodFlag == 0){
                        ProcessManageWait(ReceiveProcessManage);
                    }
                    ReceiveProcessManage.goodFlag = 0;
                    //生产操作
                    pojo_pcb.setStatue(ReceiveStatue);
                    ReceiveProcessManage.addProcess(pojo_pcb);
                    ReceiveProcessManage.goodFlag = 1;
                    if (ReceiveStatue == 3){
                        System.out.println("run create runthread!");
                        RunThread runThread = new RunThread(pojo_pcb);
                        pojo_pcb.setMyThread(runThread);
                        executor.execute(runThread);
                    }
                    ProcessManageNotifyAll(ReceiveProcessManage);
                }
                new Thread(()->{
                    Platform.runLater(()->{
                        DrawLabels(SendProcessManage);
                        DrawLabels(ReceiveProcessManage);
                    });
                }).start();
                while(!ReceiveProcessManage.IsEmpty())
                    ProcessManageWait(ReceiveProcessManage);
                }
            }
        }
        //run的消费者 ready的消费者 run的生产者 ready的生产者
    class  readyPassrunThread extends Thread {
        @Override
        public void run() {
            while (true) {
                isComingWait = true;
                //发送信号给runthread表示我已经准备好了你可以发送信号给我操作了
                LockNotifyAll(lock);
                LockWait(lock);
                isComingWait = false;
                Pojo_PCB pojo_pcb = ConsumerOfProcessManage(readyprocessmanege,null);
                IsDeleteReady = true;
                LockNotifyAll(lockOfDelete);
                //生产者部分
                if (runprocessmanege.IsEmpty()) {
                    ProduceProcessManage(runprocessmanege,pojo_pcb);
                    System.out.println("readytorun create");
                    RunThread runThread = new RunThread(pojo_pcb);
                    pojo_pcb.setMyThread(runThread);
                    executor.execute(runThread);
                }
            }
        }
    }
    class RunThread extends Thread{
        private Pojo_PCB addreadypojo_pcb;
        public RunThread(Pojo_PCB pojo_pcb){
            this.addreadypojo_pcb = pojo_pcb;
        }

        @Override
        public void run() {
            Table table = storage.getStorage(addreadypojo_pcb.getRoomOfStore());
            Label label = new Label(addreadypojo_pcb.getProcessName());
            synchronized (storage.lockOfStore){
                //获取储存
                if (table == null){
                    addreadypojo_pcb.AddHungupNumber();
                    //加入挂起队列
                    ProduceProcessManage(hungprocessmanege,addreadypojo_pcb);
                    //删除run中的对应进程
                    ConsumerOfProcessManage(runprocessmanege,addreadypojo_pcb);

                    //处理由于存储空间导致的空间占用过多的情况
                    if (!hungprocessmanege.IsEmpty()){
                        backupprocessmanege.removeAllProcess();
                        readyprocessmanege.removeAllProcess();
                        hungprocessmanege.removeAllProcess();
                        hungdownprocessmanage.removeAllProcess();
                        new Thread(()->{
                            Platform.runLater(()->{
                                labeltip1.setText("由于存储空间导致的空间占用过多已自动结束所有进程！");
                                System.out.println("由于存储空间导致的空间占用过多已自动结束所有进程！");
                                DrawLabels(backupprocessmanege);
                                DrawLabels(readyprocessmanege);
                                DrawLabels(hungprocessmanege);
                                DrawLabels(hungdownprocessmanage);
                                runprocessmanege.setOut(0);
                                DrawLabels(runprocessmanege);
                                runprocessmanege.removeAllProcess();
                                //关闭所有的线程
                                executor.shutdownNow();
                                executor = Executors.newCachedThreadPool();
                                //清空布局里面的所有Label
                                Iterator<Node> iterator = storagePane.getChildren().iterator();
                                while (iterator.hasNext()) {
                                    Node node = iterator.next();
                                    if (node instanceof Label) {
                                        iterator.remove();
                                    }
                                }
                                LockNotifyAll(LockOfHungdownAndBackup);
                                ProcessManageNotifyAll(readyprocessmanege);
                                ProcessManageNotifyAll(runprocessmanege);
                                storage.InitStorage();
                            });
                        }).start();
                        Thread.interrupted();
                        return;
                    }
                    //防止一直解挂导致的运行顺序颠倒 导致运行队列无法得到补充
                    try {
                        Thread.sleep(3);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    ProcessManageNotifyAll(runprocessmanege);

                    //加入解挂队列 若由于主存空间的空间一直不足导致一直进行挂起解挂操作超过5次 则不予进行解挂操作 放入挂起队列等待手动解挂
                    if (hungdownprocessmanage.IsEmpty() && addreadypojo_pcb.getHungupNumber() < 5){
                        addreadypojo_pcb.AddHungupNumber();
                        //挂起队列的消费者
                        ConsumerOfProcessManage(hungprocessmanege,addreadypojo_pcb);
                        //解挂队列的生产者
                        ProduceProcessManage(hungdownprocessmanage,addreadypojo_pcb);
                        ProcessManageNotifyAll(hungdownprocessmanage);
                    }
                    new Thread(()->{
                        Platform.runLater(()->{
                            labeltip1.setText("由于存储空间不够导致进程"+addreadypojo_pcb.getProcessName()+"被挂起！");
                            DrawLabels(runprocessmanege);
                            DrawLabels(hungprocessmanege);
                            DrawLabels(hungdownprocessmanage);
                        });
                    }).start();
                    Thread.interrupted();
                    return;
                }
                else {
                    new Thread(()->{
                        Platform.runLater(()->{
                            storagePane.getChildren().add(label);
                            addreadypojo_pcb.setBeginAddress(table.getBeginAddress());
                            label.setMinWidth(70);
                            label.setMinHeight(addreadypojo_pcb.getRoomOfStore() / 2);
                            label.setAlignment(Pos.CENTER);
                            label.setStyle("-fx-background-color: red;" +
                                    "-fx-border-color:white;");
                            label.setLayoutY(table.getBeginAddress()/2);
                        });
                    }).start();
                }
            }


            try {
                int number = addreadypojo_pcb.getRuntime()*1000;
                //sleep以毫秒为单位
                Thread.sleep(number);
                System.out.println("睡眠结束"+addreadypojo_pcb.getProcessName());
            } catch (InterruptedException e) {
                System.out.println("异常处理");
                addreadypojo_pcb.setMyThread(null);
                Thread.interrupted();
                return;
            }
            if (table != null){
                //解放存储空间
                storage.ReleaseStorage(table);
                new Thread(()->{
                    Platform.runLater(()->{
                        storagePane.getChildren().remove(label);
                    });
                }).start();
            }

            //休眠结束说明这个时候addreadypojo_pcb已经离开运行队列了
            addreadypojo_pcb.setMyThread(null);
            synchronized (lockOfRun){
                if (!isComingWait)
                LockWait(lock);
                //如果运行时间等于1就直接丢弃
                if (addreadypojo_pcb.getRuntime() == 1){
                    ConsumerOfProcessManage(runprocessmanege , addreadypojo_pcb);
                    //发送信息给readyPassrunThread表示它可以行动了
                    if (readyprocessmanege.IsElement()){
                        LockNotifyAll(lock);
                        //如果已经未删除则等待 已删除了就往后走
                        if (!IsDeleteReady){
                            //等待那边readyPassrunThread已经删除了ready中的一个进程之后在进行接下来的ready生产者
                            LockWait(lockOfDelete);
                        }
                        IsDeleteReady = false;
                    }
                    if (readyprocessmanege.getOut() == 5){
                        ProcessManageNotifyAll(runprocessmanege);
                    }
                    else
                        ProcessManageNotifyAll(readyprocessmanege);
                }
                else {
                    //更改run完后的信息
                    addreadypojo_pcb.setStatue(2);
                    addreadypojo_pcb.setPriority(addreadypojo_pcb.getPriority() + 1);
                    addreadypojo_pcb.setRuntime(addreadypojo_pcb.getRuntime() - 1);
                    readyprocessmanege.WaitPojoPCB = addreadypojo_pcb;
                    Pojo_PCB [] PCBList = readyprocessmanege.getPCBList();
                    if ( !readyprocessmanege.IsElement() || addreadypojo_pcb.getPriority() < PCBList[0].getPriority()
                            || addreadypojo_pcb.getPriority() == PCBList[0].getPriority() && addreadypojo_pcb.getRuntime() < PCBList[0].getRuntime()){
                        //当等待加入的PCB比最高的PCB优先级更高的时候进行交换 把它放入优先级最高等待再次加入运行队列 Wait有可能为空
                        readyprocessmanege.WaitPojoPCB = PCBList[0];
                        ConsumerOfProcessManage(readyprocessmanege,null);
                        readyprocessmanege.addProcess(addreadypojo_pcb);
                    }

                    //run的消费者
                    ConsumerOfProcessManage(runprocessmanege , addreadypojo_pcb);
                    //发送信息给readyPassrunThread表示它可以行动了
                    LockNotifyAll(lock);
                    //如果已经未删除则等待 已删除了就往后走
                    if (!IsDeleteReady){
                        //等待那边readyPassrunThread已经删除了ready中的一个进程之后在进行接下来的ready生产者
                        LockWait(lockOfDelete);
                    }
                    //归零
                    IsDeleteReady = false;

                    //ready的生产者
                    if (readyprocessmanege.goodFlag == 0){
                        count = 1;
                        ProcessManageWait(readyprocessmanege);
                    }
                    readyprocessmanege.goodFlag = 0;
                    if (readyprocessmanege.WaitPojoPCB != null){
                        readyprocessmanege.addProcess(readyprocessmanege.WaitPojoPCB);
                        readyprocessmanege.WaitPojoPCB = null;
                    }
                    readyprocessmanege.goodFlag = 1;
                    if (count == 1){
                        count = 0;
                        ProcessManageNotifyAll(readyprocessmanege);
                    }
                }
                new Thread(()->{
                    Platform.runLater(()->{
                        DrawLabels(readyprocessmanege);
                        DrawLabels(runprocessmanege);
                    });
                }).start();
            }
        }
    }
    class HungDownThread extends Thread{
        @Override
        public void run() {
            while (true){
                while (!hungdownprocessmanage.IsElement()){
                    //判断到这个时候
                    IsElementOfHungDownProcessManage = false;
                    LockNotifyAll(LockOfHungdownAndBackup);
                    ProcessManageWait(hungdownprocessmanage);
                }
                    IsElementOfHungDownProcessManage = true;
                //此时后备队列到就绪队列正在运行
                if (hungDown == 1){
                    LockWait(LockOfHungdown);
                }
                if (!readyprocessmanege.IsEmpty()){
                    ProcessManageWait(readyprocessmanege);
                }
                //解挂队列的消费者
                Pojo_PCB pojo_pcb = ConsumerOfProcessManage(hungdownprocessmanage,null);
                if (pojo_pcb != null){
                    //准备队列的生产者
                    ProduceProcessManage(readyprocessmanege,pojo_pcb);
                }
                new Thread(()->{
                    Platform.runLater(()->{
                        DrawLabels(readyprocessmanege);
                        DrawLabels(hungdownprocessmanage);
                    });
                }).start();
            }
        }
    }
}