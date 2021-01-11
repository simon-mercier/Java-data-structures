package AVLTree;

import java.util.*;

/*
  The {@code AVLTree} is a self-balancing BST.
  "In an AVL tree, the heights of the two child subtrees of any node differ by at most one;
  if at any time they differ by more than one, rebalancing is done to restore this property."
  <a href="https://en.wikipedia.org/wiki/AVL_tree">

  The implementation of this AVL tree is iterative and supports the following operations:
  <em>put</em> O(log(n))
  <em>delete</em> O(log(n))
  <em>contains</em> O(log(n))
  <em>minimum</em> O(log(n))
  <em>getHeight</em> O(1)
  <em>infixOrder</em> O(1)
  <em>levelOrder</em> O(1)
 */
public class AVLTree<ValueType extends Comparable<? super ValueType>> {
    private BinaryNode<ValueType> root;

    public void put(ValueType value) {

        if (root == null) {
            root = new BinaryNode<>(value, null);
            return;
        }

        int compareResult = 0;
        BinaryNode<ValueType> current = root;
        BinaryNode<ValueType> parent = null;
        while (current != null) {
            parent = current;
            compareResult = value.compareTo(current.value);

            if (compareResult == 0)
                return;

            current = compareResult < 0 ? current.left : current.right;
        }

        if (compareResult < 0)
            parent.left = new BinaryNode(value, parent);
        else
            parent.right = new BinaryNode(value, parent);

        balance(parent);
    }
    
    public void delete(ValueType value) {

        if (root == null)
            return;

        int compareResult = 0;
        int lastCompareResult = compareResult;
        BinaryNode<ValueType> current = root;

        while (current != null) {
            lastCompareResult = compareResult;
            compareResult = value.compareTo(current.value);

            if (compareResult == 0)
                break;

            current = compareResult < 0 ? current.left : current.right;
        }

        if (current == null)
            return;

        if (current.left != null && current.right != null) //has 2 children
        {
            BinaryNode<ValueType> minNodeOfRightTree = findMin(current.right);
            current.value = minNodeOfRightTree.value; //override this node's value with the min value of right tree
            if (minNodeOfRightTree.parent.left == minNodeOfRightTree)//remove the node
                minNodeOfRightTree.parent.left = minNodeOfRightTree.left;
            else
                minNodeOfRightTree.parent.right = minNodeOfRightTree.right;

            balance(minNodeOfRightTree.parent);
        } else //has one child or is a leaf
        {
            if (current == root) {
                root = null;
                return; //No need to balance
            }

            if (lastCompareResult < 0)//currentNode was the left child of the parent
            {
                current.parent.left = current.left;

                if (current.left != null)
                    current.left.parent = current.parent;
            } else //currentNode was the right child of the parent
            {
                current.parent.right = current.right;

                if (current.right != null)
                    current.right.parent = current.parent;
            }

            balance(current.parent);
        }
    }

    public boolean contains(ValueType value) {
        BinaryNode<ValueType> current = root;
        while (current != null) {
            int compareResult = value.compareTo(current.value);

            if (compareResult == 0)
                return true;

            current = compareResult < 0 ? current.left : current.right;
        }
        return false;
    }

    public int getHeight() {
        return root == null ? -1 : root.height;
    }
    
    public ValueType findMin() {
        BinaryNode<ValueType> minNode = findMin(root);
        return minNode != null ? minNode.value : null;
    }

    public BinaryNode<ValueType> findMin(BinaryNode<ValueType> root) {
        BinaryNode<ValueType> currentNode = root;

        if (currentNode == null)
            return null;

        while (currentNode.left != null) currentNode = currentNode.left;

        return currentNode;
    }
    
    public List<ValueType> infixOrder() {
        if (root == null)
            return null;

        BinaryNode<ValueType> currentNode = root;
        LinkedList<ValueType> list = new LinkedList<>();
        Stack<BinaryNode<ValueType>> stack = new Stack<>();

        while (currentNode != null || stack.size() > 0) {
            while (currentNode != null) {
                stack.push(currentNode);
                currentNode = currentNode.left;
            }

            currentNode = stack.pop();
            list.add(currentNode.value);
            currentNode = currentNode.right;
        }
        return list;
    }

    public List<ValueType> levelOrder() {
        if (root == null)
            return null;

        BinaryNode<ValueType> currentNode = root;
        List<ValueType> list = new LinkedList<>();
        Queue<BinaryNode<ValueType>> queue = new LinkedList<>();

        queue.add(currentNode);
        while (queue.size() > 0) {
            currentNode = queue.poll();
            list.add(currentNode.value);
            if (currentNode.left != null)
                queue.add(currentNode.left);
            if (currentNode.right != null)
                queue.add(currentNode.right);
        }
        return list;
    }
    
    private void balance(BinaryNode<ValueType> node) {
        BinaryNode<ValueType> currentNode = node;

        while (currentNode != null) {
            if (height(currentNode.left) - height(currentNode.right) > 1) {
                if (height(currentNode.left.left) < height(currentNode.left.right)) {
                    currentNode.left = rotateRight(currentNode.left);
                }
                currentNode = rotateLeft(currentNode);
            } else if (height(currentNode.right) - height(currentNode.left) > 1) {
                if (height(currentNode.right.right) < height(currentNode.right.left)) {
                    currentNode.right = rotateLeft(currentNode.right);
                }
                currentNode = rotateRight(currentNode);
            }

            currentNode.height = Math.max(height(currentNode.left), height(currentNode.right)) + 1;

            if (currentNode.parent == null)
                root = currentNode;

            currentNode = currentNode.parent;
        }
    }

    private int height(BinaryNode<ValueType> node) {
        return node == null ? -1 : node.height;
    }

    private BinaryNode<ValueType> rotateLeft(BinaryNode<ValueType> node1) { //void
        BinaryNode<ValueType> k1 = node1.left;
        node1.left = k1.right;
        k1.right = node1;
        
        k1.parent = node1.parent;
        if (node1.parent != null) {
            if (k1.right == node1.parent.left)
                k1.parent.left = k1;
            else if (k1.right == node1.parent.right)
                k1.parent.right = k1;
        }
        if (node1.left != null)
            node1.left.parent = node1;

        node1.parent = k1;
        
        node1.height = Math.max(height(node1.left), height(node1.right)) + 1;
        k1.height = Math.max(height(k1.left), node1.height) + 1;

        return k1;
    }
    
    private BinaryNode<ValueType> rotateRight(BinaryNode<ValueType> node1) { //Jai changer le void pour un binarynode, legal?
        BinaryNode<ValueType> k2 = node1.right;
        node1.right = k2.left;
        k2.left = node1;
        
        k2.parent = node1.parent;
        if (node1.parent != null) {
            if (k2.left == node1.parent.left)
                k2.parent.left = k2;
            else if (k2.left == node1.parent.right)
                k2.parent.right = k2;

        }
        if (node1.right != null)
            node1.right.parent = node1;

        node1.parent = k2;
        
        node1.height = Math.max(height(node1.left), height(node1.right)) + 1;
        k2.height = Math.max(height(k2.right), node1.height) + 1;
        return k2;
    }

    static private class BinaryNode<ValueType> {
        ValueType value;

        BinaryNode<ValueType> parent; 

        BinaryNode<ValueType> left = null;
        BinaryNode<ValueType> right = null; 

        int height = 0;

        BinaryNode(ValueType value, BinaryNode<ValueType> parent) {
            this.value = value;
            this.parent = parent;
        }
    }
}
