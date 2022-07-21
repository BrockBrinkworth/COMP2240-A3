/*
Author: Brock Brinkworth
ID: C3331952
Date Created: 15/10/2021

Program: Simulates a system that uses paging with virtual memory
*/

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Map;

import A3.Process;


public class A3 {

    /**
    * @pre input must be in form totalFrames totalTime process1.txt process2.txt ... processn.txt
    * @post this works :)
    */
    public static void run(String[] input) {

        int totalFrames = Integer.parseInt(input[0]);

        int totalTime = Integer.parseInt(input[1]);
        
        List<Process> list1 = new ArrayList<>();

        List<Process> list2 = new ArrayList<>();

        // Scan in and process files
        for (int i = 2; i < input.length; i++) {

            try {

                Scanner sc = new Scanner(new File(input[i]));

                Process newP = new Process(input[i]);

                Process newP2 = new Process(input[i]);

                sc.nextLine();

                while (sc.hasNextLine()) {

                    String s = sc.nextLine();

                    if (!s.equals("end")) { 

                        newP.add(Integer.parseInt(s));

                        newP2.add(Integer.parseInt(s));

                    }
                }

                sc.close();

                list1.add(newP);

                list2.add(newP2);
                
            } catch (Exception e) {

                //

            }
        }

        local(list1, totalFrames, totalTime);

        global(list2, totalFrames, totalTime);

    }

    public static void local(List<Process> list1, int totalFrames, int totalTime) {

        List<Entry<Process, Integer>> blocked = new ArrayList<Entry<Process, Integer>>();

        List<Entry<String, Integer>> memory = new ArrayList<>();

        Queue<Process> ready = new LinkedList<>(list1);

        List<Process> toPrint = new ArrayList<>();

        int max_frames = totalFrames/list1.size();

        int time = 0;

        int pTime = 0;

        while (!blocked.isEmpty() || !ready.isEmpty()) {

            List<Entry<Process, Integer>> newBlocked = new ArrayList<>();

            for (Entry<Process, Integer> p : blocked) {

                if (p.getValue() > 1)  {

                    newBlocked.add(Map.entry(p.getKey(), p.getValue()-1));

                } else {

                    ready.add(p.getKey());

                }
            }

            blocked = newBlocked;

            while (!ready.isEmpty()) {

                // check if page in memory
                Process p = ready.peek();

                Entry<String, Integer> e = Map.entry(p.getId(), Integer.valueOf(p.peekNextPage()));

                if (memory.contains(e)) {

                    if (pTime >= totalTime) {

                        pTime = 0;

                        ready.remove();

                        ready.add(p);

                        continue;

                    }

                    p.nextPage();

                    // reseting prio

                    //memory.remove(e);

                    //memory.add(e);

                    pTime++;
                    
                    if (p.isFinished()) {

                        p.setLastAccessed(time+1);

                        toPrint.add(p);

                        ready.remove();

                        pTime = 0;

                    }

                    break;

                } else {

                    if (pTime >= totalTime) {

                        pTime = 0;

                        ready.remove();

                        ready.add(p);

                        continue;

                    }

                    ready.remove();

                    if (memory.stream().filter(o -> o.getKey().equals(e.getKey())).collect(Collectors.toList()).size() >= max_frames) {

                        memory.remove(memory.stream().filter(o -> o.getKey().equals(e.getKey())).findFirst().get());
    
                    }

                    memory.add(e);

                    blocked.add(Map.entry(p, 6));

                    p.addFault(time);

                    pTime = 0;

                }
            }

            time++;

        }

        Collections.sort(toPrint);

        System.out.println("FIFO - Fixed-Local Replacement:\nPID  Process Name      Turnaround Time  # Faults  Fault Times");

        toPrint.stream().forEach(o -> System.out.println(o.toString()));

    }

    public static void global(List<Process> list1, int totalFrames, int totalTime) {

        List<Entry<Process, Integer>> blocked = new ArrayList<Entry<Process, Integer>>();

        List<Entry<String, Integer>> memory = new ArrayList<>();

        Queue<Process> ready = new LinkedList<>(list1);

        List<Process> toPrint = new ArrayList<>();

        int time = 0;

        int pTime = 0;

        while (!blocked.isEmpty() || !ready.isEmpty()) {

            List<Entry<Process, Integer>> newBlocked = new ArrayList<>();

            for (Entry<Process, Integer> p : blocked) {

                if (p.getValue() > 1)  {

                    newBlocked.add(Map.entry(p.getKey(), p.getValue()-1));

                } else {

                    ready.add(p.getKey());

                }
            }

            blocked = newBlocked;

            while (!ready.isEmpty()) {

                // check if page in memory
                Process p = ready.peek();

                Entry<String, Integer> e = Map.entry(p.getId(), Integer.valueOf(p.peekNextPage()));

                if (memory.contains(e)) {
                    
                    if (pTime >= totalTime) {

                        pTime = 0;

                        ready.remove();

                        ready.add(p);

                        continue;

                    }

                    p.nextPage();

                    // reseting prio

                    //memory.remove(e);

                    //memory.add(e);

                    pTime++;
                    
                    if (p.isFinished()) {

                        p.setLastAccessed(time+1);

                        memory = memory.stream().filter(o -> !o.getKey().equals(e.getKey())).collect(Collectors.toList());

                        toPrint.add(p);

                        ready.remove();

                        pTime = 0;

                    }

                    break;

                }

                else {

                    if (pTime >= totalTime) {

                        pTime = 0;

                        ready.remove();

                        ready.add(p);

                        continue;

                    }

                    if (memory.size() >= totalFrames) {

                        memory.remove(memory.stream().filter(o ->   o.getKey().equals(e.getKey())).findFirst().get());

                    }

                    ready.remove();

                    memory.add(e);

                    blocked.add(Map.entry(p, 6));

                    p.addFault(time);

                    pTime = 0;

                }
            }   

            time++;

        }

        Collections.sort(toPrint);

        System.out.println("FIFO - Variable-Global Replacement:\nPID  Process Name      Turnaround Time  # Faults  Fault Times");

        toPrint.stream().forEach(o -> System.out.println(o.toString()));
    }
    public static void main(String[] args) {

        /*String[] input = {"50", "3", "process1.txt", "process2.txt", "process3.txt", "process4.txt", "process5.txt",
            "process6.txt", "process7.txt", "process8.txt", "process9.txt", "process10.txt"
        };*/ // Testing

        //run(input); // Testing

        run(args);

    }
}