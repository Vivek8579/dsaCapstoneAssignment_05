# dsaCapstoneAssignment_05
this is my dsa capstone assignment i made this in 3rd semester during my B..tech prepration. and this is a dsa assignment number 5 and project is on Hospital Asspointment &amp; Triage System.
🏥 Hospital Token Management System

A Java-based console application for managing patients, doctors, routine queues, emergency queues, appointment slots, and undo operations.
Designed using custom data structures for efficient real-world simulation.

✨ Features
👨‍⚕️ Patient & Doctor Management

Register / update patients

Track visit history

Add doctors with specialization

🕒 Slot Management

Add appointment slots

Cancel or restore slots

Check next available slot

🎫 Token System

Routine booking (FIFO using Circular Queue)

Emergency booking (Min-Heap based on severity)

Serve next patient with emergency priority

🔄 Undo Support

Undo routine booking

Undo emergency insertion

Undo serve actions

Undo slot cancellation

📊 Reports

Pending tokens per doctor

Served routine vs. emergency counts

Top 3 frequent patients

🧱 Data Structures Used

🗂 Hash Table → Patient indexing

🔗 Linked List → Doctor slot list

🔄 Circular Queue → Routine patients

⚠️ Min Heap → Emergency patients

📦 Stack → Undo operations

`📜 Menu Options`
1  Register / Update Patient  
2  Add Doctor  
3  Add Slot  
4  Book Routine Slot  
5  Serve Next  
6  Emergency In  
7  Cancel Slot  
8  Undo  
9  Reports  
0  Exit  

▶️ How to Run

Install Java 8+

Compile:

javac one.java


`Run:`

java one

📂 Project Structure Overview

PatientIndex → Stores patient records

DoctorTable → Stores doctors & slot lists

CircularQueue → Handles routine queue

MinHeap → Manages emergency queue

ActionStack → Saves undo actions

Token, Patient, Doctor, SlotNode → Core models

`📝 Notes`

Emergency severity rule: Lower value = more critical

Undo system ensures safe rollback of operations

Ideal for learning data structures through a real-world case


Author and owner of this file
``vivek rai 
student of 3rd semseter
at KR Mangalam University.``
