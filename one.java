import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class one {
    enum Type {ROUTINE, EMERGENCY}

    static class Patient {
        int id;
        String name;
        int age;
        int severity;
        int visits;
    }

    static class Token {
        int tokenId;
        int patientId;
        int doctorId;
        int slotId;
        Type type;
        int severity;
    }

    static class PatientIndex {
        static class Node {
            Patient p;
            Node next;
            Node(Patient p) { this.p = p; }
        }
        Node[] buckets;
        int m;
        PatientIndex(int m) {
            this.m = m;
            buckets = new Node[m];
        }
        int hash(int id) {
            int v = id % m;
            if (v < 0) v += m;
            return v;
        }
        void upsert(Patient p) {
            int h = hash(p.id);
            Node cur = buckets[h], prev = null;
            while (cur != null) {
                if (cur.p.id == p.id) {
                    cur.p.name = p.name;
                    cur.p.age = p.age;
                    cur.p.severity = p.severity;
                    return;
                }
                prev = cur;
                cur = cur.next;
            }
            Node n = new Node(p);
            if (prev == null) buckets[h] = n; else prev.next = n;
        }
        Patient get(int id) {
            int h = hash(id);
            Node cur = buckets[h];
            while (cur != null) {
                if (cur.p.id == id) return cur.p;
                cur = cur.next;
            }
            return null;
        }
        List<Patient> all() {
            List<Patient> out = new ArrayList<>();
            for (Node b : buckets) {
                Node c = b;
                while (c != null) {
                    out.add(c.p);
                    c = c.next;
                }
            }
            return out;
        }
    }

    static class CircularQueue {
        Token[] data;
        int front = 0;
        int rear = -1;
        int size = 0;
        int cap;
        CircularQueue(int cap) {
            this.cap = cap;
            data = new Token[cap];
        }
        boolean isFull() { return size == cap; }
        boolean isEmpty() { return size == 0; }
        void enqueue(Token t) {
            if (isFull()) throw new IllegalStateException("Queue full");
            rear = (rear + 1) % cap;
            data[rear] = t;
            size++;
        }
        Token dequeue() {
            if (isEmpty()) return null;
            Token t = data[front];
            data[front] = null;
            front = (front + 1) % cap;
            size--;
            return t;
        }
        Token peek() {
            if (isEmpty()) return null;
            return data[front];
        }
        void pushFront(Token t) {
            if (isFull()) throw new IllegalStateException("Queue full");
            front = (front - 1 + cap) % cap;
            data[front] = t;
            size++;
        }
        Token popBack() {
            if (isEmpty()) return null;
            Token t = data[rear];
            data[rear] = null;
            rear = (rear - 1 + cap) % cap;
            size--;
            return t;
        }
    }

    static class MinHeap {
        List<Token> arr = new ArrayList<>();
        void insert(Token t) {
            arr.add(t);
            up(arr.size() - 1);
        }
        Token extractMin() {
            if (arr.isEmpty()) return null;
            Token min = arr.get(0);
            Token last = arr.remove(arr.size() - 1);
            if (!arr.isEmpty()) {
                arr.set(0, last);
                down(0);
            }
            return min;
        }
        void up(int i) {
            while (i > 0) {
                int p = (i - 1) / 2;
                if (arr.get(p).severity <= arr.get(i).severity) break;
                swap(p, i);
                i = p;
            }
        }
        void down(int i) {
            int n = arr.size();
            while (true) {
                int l = 2 * i + 1, r = 2 * i + 2, s = i;
                if (l < n && arr.get(l).severity < arr.get(s).severity) s = l;
                if (r < n && arr.get(r).severity < arr.get(s).severity) s = r;
                if (s == i) break;
                swap(s, i);
                i = s;
            }
        }
        void swap(int a, int b) {
            Token t = arr.get(a);
            arr.set(a, arr.get(b));
            arr.set(b, t);
        }
        boolean remove(Token t) {
            for (int i = 0; i < arr.size(); i++) {
                Token x = arr.get(i);
                if (x.tokenId == t.tokenId) {
                    Token last = arr.remove(arr.size() - 1);
                    if (i < arr.size()) {
                        arr.set(i, last);
                        down(i);
                        up(i);
                    }
                    return true;
                }
            }
            return false;
        }
        boolean isEmpty() {
            return arr.isEmpty();
        }
    }

    static class SlotNode {
        int slotId;
        String start;
        String end;
        String status;
        SlotNode next;
        SlotNode(int slotId, String start, String end) {
            this.slotId = slotId;
            this.start = start;
            this.end = end;
            this.status = "FREE";
        }
    }

    static class Doctor {
        int id;
        String name;
        String specialization;
        SlotNode head;
        void addSlot(int slotId, String start, String end) {
            SlotNode n = new SlotNode(slotId, start, end);
            if (head == null) {
                head = n;
                return;
            }
            SlotNode cur = head;
            while (cur.next != null) cur = cur.next;
            cur.next = n;
        }
        boolean cancelSlot(int slotId) {
            SlotNode cur = head, prev = null;
            while (cur != null) {
                if (cur.slotId == slotId) {
                    if (prev == null) head = cur.next; else prev.next = cur.next;
                    return true;
                }
                prev = cur;
                cur = cur.next;
            }
            return false;
        }
        SlotNode nextFree() {
            SlotNode cur = head;
            while (cur != null) {
                if (cur.status.equals("FREE")) return cur;
                cur = cur.next;
            }
            return null;
        }
        int pendingCount() {
            int c = 0;
            SlotNode cur = head;
            while (cur != null) {
                if (cur.status.equals("BOOKED")) c++;
                cur = cur.next;
            }
            return c;
        }
    }

    static class UndoAction {
        String type;
        Token token;
        SlotNode slotRef;
        int doctorId;
        UndoAction(String type, Token token) {
            this.type = type;
            this.token = token;
        }
        UndoAction(String type, SlotNode slot, int doctorId) {
            this.type = type;
            this.slotRef = slot;
            this.doctorId = doctorId;
        }
    }

    static class ActionStack {
        List<UndoAction> arr = new ArrayList<>();
        void push(UndoAction a) { arr.add(a); }
        UndoAction pop() {
            if (arr.isEmpty()) return null;
            return arr.remove(arr.size() - 1);
        }
        boolean isEmpty() { return arr.isEmpty(); }
    }

    static class DoctorTable {
        List<Doctor> docs = new ArrayList<>();
        Doctor get(int id) {
            for (Doctor d : docs) if (d.id == id) return d;
            return null;
        }
        void add(Doctor d) {
            if (get(d.id) == null) docs.add(d);
        }
        List<Doctor> all() { return docs; }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        PatientIndex patients = new PatientIndex(101);
        DoctorTable doctors = new DoctorTable();
        CircularQueue routine = new CircularQueue(200);
        MinHeap emergency = new MinHeap();
        ActionStack undo = new ActionStack();
        int tokenSeq = 1;
        int servedRoutine = 0;
        int servedEmergency = 0;
        while (true) {
            System.out.println("1 Register/Update Patient");
            System.out.println("2 Add Doctor");
            System.out.println("3 Add Slot");
            System.out.println("4 Book Routine Slot");
            System.out.println("5 Serve Next");
            System.out.println("6 Emergency In");
            System.out.println("7 Cancel Slot");
            System.out.println("8 Undo");
            System.out.println("9 Reports");
            System.out.println("0 Exit");
            int choice = readInt(sc);
            if (choice == 0) break;
            if (choice == 1) {
                System.out.print("Patient id: ");
                int id = readInt(sc);
                System.out.print("Name: ");
                String name = sc.nextLine();
                System.out.print("Age: ");
                int age = readInt(sc);
                Patient p = new Patient();
                p.id = id;
                p.name = name;
                p.age = age;
                p.severity = 0;
                p.visits = 0;
                patients.upsert(p);
                System.out.println("Saved");
            } else if (choice == 2) {
                System.out.print("Doctor id: ");
                int id = readInt(sc);
                System.out.print("Name: ");
                String name = sc.nextLine();
                System.out.print("Specialization: ");
                String sp = sc.nextLine();
                Doctor d = new Doctor();
                d.id = id;
                d.name = name;
                d.specialization = sp;
                doctors.add(d);
                System.out.println("Doctor added");
            } else if (choice == 3) {
                System.out.print("Doctor id: ");
                int did = readInt(sc);
                Doctor d = doctors.get(did);
                if (d == null) {
                    System.out.println("No doctor");
                    continue;
                }
                System.out.print("Slot id: ");
                int sid = readInt(sc);
                System.out.print("Start: ");
                String st = sc.nextLine();
                System.out.print("End: ");
                String en = sc.nextLine();
                d.addSlot(sid, st, en);
                System.out.println("Slot added");
            } else if (choice == 4) {
                System.out.print("Patient id: ");
                int pid = readInt(sc);
                Patient p = patients.get(pid);
                if (p == null) {
                    System.out.println("Register first");
                    continue;
                }
                System.out.print("Doctor id: ");
                int did = readInt(sc);
                Doctor d = doctors.get(did);
                if (d == null) {
                    System.out.println("No doctor");
                    continue;
                }
                SlotNode slot = d.nextFree();
                if (slot == null) {
                    System.out.println("No free slot");
                    continue;
                }
                slot.status = "BOOKED";
                Token t = new Token();
                t.tokenId = tokenSeq++;
                t.patientId = pid;
                t.doctorId = did;
                t.slotId = slot.slotId;
                t.type = Type.ROUTINE;
                routine.enqueue(t);
                undo.push(new UndoAction("book", t));
                System.out.println("Booked token " + t.tokenId);
            } else if (choice == 5) {
                Token served = emergency.extractMin();
                String from = "emergency";
                if (served == null) {
                    served = routine.dequeue();
                    from = "routine";
                }
                if (served == null) {
                    System.out.println("No patients");
                    continue;
                }
                Patient p = patients.get(served.patientId);
                if (p != null) p.visits++;
                if (from.equals("emergency")) servedEmergency++; else servedRoutine++;
                undo.push(new UndoAction(from.equals("emergency") ? "serveEmergency" : "serveRoutine", served));
                Doctor d = doctors.get(served.doctorId);
                if (d != null) {
                    SlotNode cur = d.head;
                    while (cur != null) {
                        if (cur.slotId == served.slotId) {
                            cur.status = "SERVED";
                            break;
                        }
                        cur = cur.next;
                    }
                }
                System.out.println("Served token " + served.tokenId);
            } else if (choice == 6) {
                System.out.print("Patient id: ");
                int pid = readInt(sc);
                Patient p = patients.get(pid);
                if (p == null) {
                    System.out.println("Register first");
                    continue;
                }
                System.out.print("Severity (lower=critical): ");
                int sev = readInt(sc);
                System.out.print("Doctor id: ");
                int did = readInt(sc);
                System.out.print("Slot id (use -1 if none): ");
                int sid = readInt(sc);
                Token t = new Token();
                t.tokenId = tokenSeq++;
                t.patientId = pid;
                t.doctorId = did;
                t.slotId = sid;
                t.type = Type.EMERGENCY;
                t.severity = sev;
                emergency.insert(t);
                undo.push(new UndoAction("emergency", t));
                System.out.println("Emergency queued " + t.tokenId);
            } else if (choice == 7) {
                System.out.print("Doctor id: ");
                int did = readInt(sc);
                Doctor d = doctors.get(did);
                if (d == null) {
                    System.out.println("No doctor");
                    continue;
                }
                System.out.print("Slot id: ");
                int sid = readInt(sc);
                boolean ok = d.cancelSlot(sid);
                if (ok) {
                    SlotNode ref = new SlotNode(sid, "", "");
                    undo.push(new UndoAction("cancelSlot", ref, did));
                    System.out.println("Slot removed");
                } else {
                    System.out.println("Not found");
                }
            } else if (choice == 8) {
                UndoAction a = undo.pop();
                if (a == null) {
                    System.out.println("Nothing to undo");
                    continue;
                }
                if (a.type.equals("book")) {
                    Token t = routine.popBack();
                    if (t != null) {
                        Doctor d = doctors.get(t.doctorId);
                        if (d != null) {
                            SlotNode cur = d.head;
                            while (cur != null) {
                                if (cur.slotId == t.slotId) {
                                    cur.status = "FREE";
                                    break;
                                }
                                cur = cur.next;
                            }
                        }
                    }
                    System.out.println("Book undone");
                } else if (a.type.equals("emergency")) {
                    emergency.remove(a.token);
                    System.out.println("Emergency undone");
                } else if (a.type.equals("serveRoutine")) {
                    routine.pushFront(a.token);
                    Doctor d = doctors.get(a.token.doctorId);
                    if (d != null) {
                        SlotNode cur = d.head;
                        while (cur != null) {
                            if (cur.slotId == a.token.slotId) {
                                cur.status = "BOOKED";
                                break;
                            }
                            cur = cur.next;
                        }
                    }
                    if (servedRoutine > 0) servedRoutine--;
                    Patient p = patients.get(a.token.patientId);
                    if (p != null && p.visits > 0) p.visits--;
                    System.out.println("Serve undone");
                } else if (a.type.equals("serveEmergency")) {
                    emergency.insert(a.token);
                    if (servedEmergency > 0) servedEmergency--;
                    Patient p = patients.get(a.token.patientId);
                    if (p != null && p.visits > 0) p.visits--;
                    System.out.println("Serve undone");
                } else if (a.type.equals("cancelSlot")) {
                    Doctor d = doctors.get(a.doctorId);
                    if (d != null) {
                        d.addSlot(a.slotRef.slotId, "", "");
                    }
                    System.out.println("Slot restored");
                }
            } else if (choice == 9) {
                System.out.println("Per doctor pending and next free:");
                for (Doctor d : doctors.all()) {
                    SlotNode next = d.nextFree();
                    String nxt = next == null ? "none" : String.valueOf(next.slotId);
                    System.out.println("Doctor " + d.id + " pending " + d.pendingCount() + " next " + nxt);
                }
                System.out.println("Served routine: " + servedRoutine + " emergency: " + servedEmergency);
                List<Patient> all = patients.all();
                all.sort((a, b) -> b.visits - a.visits);
                int k = Math.min(3, all.size());
                System.out.println("Top patients:");
                for (int i = 0; i < k; i++) {
                    Patient p = all.get(i);
                    System.out.println(p.id + " " + p.name + " visits " + p.visits);
                }
            } else {
                System.out.println("Invalid");
            }
        }
        sc.close();
    }

    static int readInt(Scanner sc) {
        while (true) {
            String line = sc.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (Exception e) {
                System.out.print("Enter number: ");
            }
        }
    }
}

