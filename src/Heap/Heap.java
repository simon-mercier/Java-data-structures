package Heap;

import java.util.*;
/*
  The implementation of this Heap supports min and max heaps.
  The class {@code Heap} can build a heap in O(n) time complexity and can sort an array in O(nlogn)

  The class {@code HashMap} supports the following operations:
  <em>put</em> O(log(n))
  <em>pop</em> O(log(n))
  <em>peek</em> O(1)
  <em>size</em> O(1)
  <em>sort</em> O(nlog(n))

  The class {@code HashMap} also implements an Heap iterator.
 */
public class Heap<ValueType extends Comparable<? super ValueType>> implements Iterable<ValueType> {
    private ArrayList<ValueType> data;
    private boolean isMin;

    public Heap() {
        this(true);
    }

    public Heap(boolean isMin) {
        this.isMin = isMin;
        data = new ArrayList<>();
    }

    public Heap(Collection<ValueType> data) {
        this(true, data);
    }

    public Heap(boolean isMin, Collection<ValueType> data) {
        this.isMin = isMin;
        this.data = new ArrayList<>(data);
        build();
    }

    public int size() {
        // TODO
        return data.size();
    }

    private boolean compare(ValueType first, ValueType second) {
        if(isMin)
            return first.compareTo(second) > 0;
        else
            return first.compareTo(second) < 0;
    }

    private boolean shouldSwap(int childIdx, int parentIdx)
    {
        return !compare(data.get(childIdx), data.get(parentIdx));
    }

    private boolean leftShouldSwap(int lIdx, int rIdx)
    {
        return !compare(data.get(lIdx), data.get(rIdx));
    }

    private int parentIdx(int idx) {
        // TODO
        return (idx+1)/2 - 1;
    }

    private int leftChildIdx(int idx) {
        // TODO
        return rightChildIdx(idx) - 1;
    }

    private int rightChildIdx(int idx) {
        // TODO
        return (idx+1)*2;
    }

    private void swap(int firstIdx, int secondIdx) {
        // TODO
        ValueType temp = data.get(firstIdx);
        data.set(firstIdx, data.get(secondIdx));
        data.set(secondIdx, temp);
    }

    private void heapify(int idx) {
        // TODO
        if(idx < 0)
            return;

        int lIdx = leftChildIdx(idx), rIdx = rightChildIdx(idx);

        //On regarde dans l enfant de gauche
        if (lIdx < data.size() && shouldSwap(lIdx, idx))
        {
            swap(lIdx, idx);
            heapify(parentIdx(idx));
        }

        //On regarde dans l enfant de droite
        if (rIdx < data.size() && shouldSwap(rIdx, idx))
        {
            swap(rIdx, idx);
            heapify(parentIdx(idx));
        }
    }

    public void put(ValueType element) {
        data.add(element);
        if(size() == 1)
            return;
        heapify(parentIdx(data.size()-1));
    }

    public void build() {
        // TODO
        for(int i = size()/2; i >= 0; i--)
            percolateDown(i);
    }

    public ValueType pop() {
        ValueType root = data.get(0);
        data.set(0, data.get(size()-1));
        data.remove(size()-1);
        percolateDown(0);
        return root;
    }
    private void percolateDown(int idx) {
        if (idx >= size())
            return;

        int lIdx = leftChildIdx(idx);
        int rIdx = rightChildIdx(idx);
        if (rIdx < size()) // 2 enfants
        {
            int idxToSwap = leftShouldSwap(lIdx, rIdx) ? lIdx : rIdx;
            if(shouldSwap(idxToSwap, idx))
            {
                swap(idx, idxToSwap);
                percolateDown(idxToSwap);
            }
        }
        else if (lIdx < size() && shouldSwap(lIdx, idx)) // il a juste l enfant de gauche
        {
            swap(idx, lIdx);
            percolateDown(lIdx);
        }
    }

    public ValueType peek() {
        // TODO
        return data.get(0);
    }

    public List<ValueType> sort() {
        // TODO
        List<ValueType> sortedList = new ArrayList<>();
        while(data.size() > 0) sortedList.add(pop());
        return sortedList;
    }

    @Override
    public Iterator<ValueType> iterator() {
        return data.iterator();
    }
}
