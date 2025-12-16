/*
 * Copyright (c) 2014, NTUU KPI, Computer systems department and/or its affiliates. All rights reserved.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 */

package ua.kpi.comsys.test2.implementation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.math.BigInteger;

import ua.kpi.comsys.test2.NumberList;

/**
 * Custom implementation of INumberList interface.
 * Has to be implemented by each student independently.
 *
 * @author Рокицький О. С.
 * Група: ІО-33
 * № заліковки: 3320
 * Варіант: Кільцевий двонаправлений список, Двійкова система, Множення.
 */
public class NumberListImpl implements NumberList {

    // Номер заліковки згідно завдання
    private static final int RECORD_BOOK_NUMBER = 3320;

    // Система числення за замовчуванням - двійкова (С5 = 0)
    private int base = 2;

    // Власний клас вузла для двозв'язного списку
    private class Node {
        Byte value;
        Node next;
        Node prev;

        Node(Byte value) {
            this.value = value;
        }
    }

    private Node head;
    private int size;

    /**
     * Default constructor. Returns empty <tt>NumberListImpl</tt>
     */
    public NumberListImpl() {
        this.size = 0;
        this.head = null;
    }

    /**
     * Конструктор для створення списку з іншою системою числення (для changeScale).
     */
    private NumberListImpl(int base) {
        this();
        this.base = base;
    }

    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * from file, defined in string format.
     *
     * @param file - file where number is stored.
     */
    public NumberListImpl(File file) {
        this();
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (!lines.isEmpty()) {
                String content = lines.get(0).trim();
                initFromDecimalString(content);
            }
        } catch (IOException e) {
            // Ігноруємо помилки зчитування згідно логіки тестів (залишаємо список порожнім)
        }
    }

    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * in string notation.
     *
     * @param value - number in string notation.
     */
    public NumberListImpl(String value) {
        this();
        initFromDecimalString(value);
    }

    // Допоміжний метод ініціалізації
    private void initFromDecimalString(String value) {
        if (value == null) return;
        value = value.trim();
        try {
            if (!value.matches("-?\\d+")) return;

            BigInteger bi = new BigInteger(value);
            // Тести вимагають порожній список для від'ємних чисел
            if (bi.compareTo(BigInteger.ZERO) < 0) return;

            // Конвертуємо число у поточну систему числення (base = 2)
            String converted = bi.toString(this.base);
            for (int i = 0; i < converted.length(); i++) {
                int digit = Character.digit(converted.charAt(i), this.base);
                add((byte) digit);
            }
        } catch (NumberFormatException e) {
            // Ігноруємо некоректні дані
        }
    }

    /**
     * Saves the number, stored in the list, into specified file
     * in <b>decimal</b> scale of notation.
     *
     * @param file - file where number has to be stored.
     */
    public void saveList(File file) {
        try {
            String decimalString = toDecimalString();
            if (decimalString != null) {
                Files.write(file.toPath(), decimalString.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns student's record book number.
     * @return student's record book number.
     */
    public static int getRecordBookNumber() {
        return RECORD_BOOK_NUMBER;
    }

    /**
     * Change Scale implementation.
     * Uses Additional system: (0 + 1) mod 5 = 1 -> Ternary (Base 3).
     * @return <tt>NumberListImpl</tt> in Ternary scale.
     */
    public NumberListImpl changeScale() {
        String decimal = toDecimalString();
        // Якщо список порожній, повертаємо порожній список з базою 3
        if (decimal == null || decimal.isEmpty()) return new NumberListImpl(3);

        BigInteger bi = new BigInteger(decimal);

        NumberListImpl newList = new NumberListImpl(3); // База 3 (Трійкова)
        String tern = bi.toString(3);

        for (int i = 0; i < tern.length(); i++) {
            int digit = Character.digit(tern.charAt(i), 3);
            newList.add((byte) digit);
        }

        return newList;
    }

    /**
     * Additional Operation: Multiplication (C7 = 2).
     * @param arg - second argument
     * @return result of multiplication (new list).
     */
    public NumberListImpl additionalOperation(NumberList arg) {
        String s1 = this.toDecimalString();
        String s2 = ((NumberListImpl) arg).toDecimalString();

        BigInteger b1 = new BigInteger(s1.isEmpty() ? "0" : s1);
        BigInteger b2 = new BigInteger(s2.isEmpty() ? "0" : s2);

        // Операція множення
        BigInteger res = b1.multiply(b2);

        // Результат повертаємо у початковій (двійковій) системі
        NumberListImpl resultList = new NumberListImpl(); // Default base = 2
        String resStr = res.toString(2);
        for (int i = 0; i < resStr.length(); i++) {
            resultList.add((byte) Character.digit(resStr.charAt(i), 2));
        }

        return resultList;
    }

    /**
     * Returns string representation in <b>decimal</b> scale.
     */
    public String toDecimalString() {
        if (isEmpty()) return "";

        BigInteger bi = BigInteger.ZERO;
        BigInteger baseBi = BigInteger.valueOf(this.base);

        // Алгоритм Горнера для переведення в десяткову
        for (Byte b : this) {
            bi = bi.multiply(baseBi).add(BigInteger.valueOf(b));
        }

        return bi.toString(10);
    }

    @Override
    public String toString() {
        if (isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Byte b : this) {
            sb.append(b);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumberList)) return false;
        NumberList that = (NumberList) o;
        if (this.size() != that.size()) return false;
        Iterator<Byte> i1 = this.iterator();
        Iterator<Byte> i2 = that.iterator();
        while (i1.hasNext()) {
            if (!Objects.equals(i1.next(), i2.next())) return false;
        }
        return true;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        for (Byte b : this) {
            if (Objects.equals(b, o)) return true;
        }
        return false;
    }

    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private Node current = head;
            private int counter = 0;

            @Override
            public boolean hasNext() {
                return counter < size;
            }

            @Override
            public Byte next() {
                if (!hasNext()) throw new NoSuchElementException();
                Byte val = current.value;
                current = current.next;
                counter++;
                return val;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        int i = 0;
        for (Byte b : this) {
            arr[i++] = b;
        }
        return arr;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        // Цей метод виключений із завдання ("крім public <T> T[] toArray(T[] a)")
        return a;
    }

    @Override
    public boolean add(Byte e) {
        if (e == null) return false;
        Node newNode = new Node(e);
        if (head == null) {
            head = newNode;
            head.next = head;
            head.prev = head;
        } else {
            Node tail = head.prev;
            tail.next = newNode;
            newNode.prev = tail;
            newNode.next = head;
            head.prev = newNode;
        }
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (isEmpty()) return false;
        Node current = head;
        // Проходимо по списку рівно size разів, щоб не зациклитись
        for (int i = 0; i < size; i++) {
            if (Objects.equals(current.value, o)) {
                removeNode(current);
                return true;
            }
            current = current.next;
        }
        return false;
    }

    private void removeNode(Node n) {
        if (size == 1) {
            head = null;
        } else {
            n.prev.next = n.next;
            n.next.prev = n.prev;
            if (n == head) {
                head = n.next;
            }
        }
        size--;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (!contains(e)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        boolean modified = false;
        for (Byte e : c) {
            if (add(e)) modified = true;
        }
        return modified;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();
        boolean modified = false;
        for (Byte e : c) {
            add(index++, e);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object e : c) {
            while (remove(e)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        if (isEmpty()) return false;

        Node curr = head;
        int s = size;
        for (int i = 0; i < s; i++) {
            Node next = curr.next;
            if (!c.contains(curr.value)) {
                removeNode(curr);
                modified = true;
            }
            curr = next;
        }
        return modified;
    }

    @Override
    public void clear() {
        head = null;
        size = 0;
    }

    @Override
    public Byte get(int index) {
        return getNode(index).value;
    }

    @Override
    public Byte set(int index, Byte element) {
        Node curr = getNode(index);
        Byte old = curr.value;
        curr.value = element;
        return old;
    }

    private Node getNode(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        Node curr = head;
        // Оптимізація пошуку: йти з голови або з хвоста
        if (index <= size / 2) {
            for (int i = 0; i < index; i++) curr = curr.next;
        } else {
            curr = head.prev;
            for (int i = size - 1; i > index; i--) curr = curr.prev;
        }
        return curr;
    }

    @Override
    public void add(int index, Byte element) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();
        if (index == size) {
            add(element);
        } else {
            Node newNode = new Node(element);
            Node current = getNode(index);
            Node prev = current.prev;

            prev.next = newNode;
            newNode.prev = prev;
            newNode.next = current;
            current.prev = newNode;

            if (index == 0) head = newNode;
            size++;
        }
    }

    @Override
    public Byte remove(int index) {
        Node n = getNode(index);
        Byte val = n.value;
        removeNode(n);
        return val;
    }

    @Override
    public int indexOf(Object o) {
        if (isEmpty()) return -1;
        Node curr = head;
        for (int i = 0; i < size; i++) {
            if (Objects.equals(curr.value, o)) return i;
            curr = curr.next;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (isEmpty()) return -1;
        Node curr = head.prev; // Починаємо з хвоста (ефективно для двозв'язного)
        for (int i = size - 1; i >= 0; i--) {
            if (Objects.equals(curr.value, o)) return i;
            curr = curr.prev;
        }
        return -1;
    }

    @Override
    public ListIterator<Byte> listIterator() {
        // Повертаємо ітератор для навігації
        return new ListIterator<Byte>() {
            private Node current = head;
            private int index = 0;

            @Override
            public boolean hasNext() { return index < size; }

            @Override
            public Byte next() {
                if (!hasNext()) throw new NoSuchElementException();
                Byte val = current.value;
                current = current.next;
                index++;
                return val;
            }

            @Override
            public boolean hasPrevious() { return index > 0; }

            @Override
            public Byte previous() {
                if (!hasPrevious()) throw new NoSuchElementException();
                current = current.prev;
                index--;
                return current.value;
            }

            @Override
            public int nextIndex() { return index; }

            @Override
            public int previousIndex() { return index - 1; }

            @Override
            public void remove() { throw new UnsupportedOperationException(); }

            @Override
            public void set(Byte e) { throw new UnsupportedOperationException(); }

            @Override
            public void add(Byte e) { throw new UnsupportedOperationException(); }
        };
    }

    @Override
    public ListIterator<Byte> listIterator(int index) {
        ListIterator<Byte> it = listIterator();
        // Перемотуємо до потрібного індексу
        while (it.nextIndex() < index && it.hasNext()) {
            it.next();
        }
        return it;
    }

    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }
        // Повертаємо копію підсписку (найбезпечніший варіант для даної задачі)
        NumberListImpl sub = new NumberListImpl();
        sub.base = this.base;
        Node curr = head;
        // Доходимо до fromIndex
        for (int i=0; i<fromIndex; i++) curr = curr.next;
        // Копіюємо елементи
        for (int i=fromIndex; i<toIndex; i++) {
            sub.add(curr.value);
            curr = curr.next;
        }
        return sub;
    }

    @Override
    public boolean swap(int index1, int index2) {
        if (index1 < 0 || index1 >= size || index2 < 0 || index2 >= size) return false;
        if (index1 == index2) return true;

        Node n1 = getNode(index1);
        Node n2 = getNode(index2);

        Byte temp = n1.value;
        n1.value = n2.value;
        n2.value = temp;

        return true;
    }

    @Override
    public void sortAscending() {
        if (size <= 1) return;
        boolean swapped;
        do {
            swapped = false;
            Node current = head;
            for (int i = 0; i < size - 1; i++) {
                if (current.value > current.next.value) {
                    Byte tmp = current.value;
                    current.value = current.next.value;
                    current.next.value = tmp;
                    swapped = true;
                }
                current = current.next;
            }
        } while (swapped);
    }

    @Override
    public void sortDescending() {
        if (size <= 1) return;
        boolean swapped;
        do {
            swapped = false;
            Node current = head;
            for (int i = 0; i < size - 1; i++) {
                if (current.value < current.next.value) {
                    Byte tmp = current.value;
                    current.value = current.next.value;
                    current.next.value = tmp;
                    swapped = true;
                }
                current = current.next;
            }
        } while (swapped);
    }

    @Override
    public void shiftLeft() {
        // Циклічний зсув вліво: Head зміщується на наступний елемент.
        if (size > 1) {
            head = head.next;
        }
    }

    @Override
    public void shiftRight() {
        // Циклічний зсув вправо: Head зміщується на попередній елемент.
        if (size > 1) {
            head = head.prev;
        }
    }
}
