/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2012
//
// Copyright in this software belongs to University of Southampton
// IT Innovation Centre of Gamma House, Enterprise Road, 
// Chilworth Science Park, Southampton, SO16 7NS, UK.
//
// This software may not be used, sold, licensed, transferred, copied
// or reproduced in whole or in part in any manner or form or in or
// on any media by any person other than in accordance with the terms
// of the Licence Agreement supplied with the software, or otherwise
// without the prior written consent of the copyright owners.
//
// This software is distributed WITHOUT ANY WARRANTY, without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE, except where stated in the Licence Agreement supplied with
// the software.
//
//      Created By :            Vegard Engen
//      Created Date :          2011-06-21
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
//
//  Dependencies: Modified code from Salmon Run, shared freely online at:
//                http://sujitpal.blogspot.com/2006/05/java-data-structure-generic-tree.html
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.common.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node of the Tree<T> class. The Node<T> is also a container, and
 * can be thought of as instrumentation to determine the location of the type T
 * in the Tree<T>.
 */
public class Node<T> {
 
//    public T data; // removed to avoid confusion about how to use this class
    public List<Node<T>> children;
 
    /**
     * Default ctor.
     */
    public Node() {
        super();
    }
 
    // removed to avoid confusion about how to use this class
    /**
     * Convenience ctor to create a Node<T> with an instance of T.
     * @param data an instance of T.
     *
    public Node(T data) {
        this();
        setData(data);
    }/
     
    /**
     * Return the children of Node<T>. The Tree<T> is represented by a single
     * root Node<T> whose children are represented by a List<Node<T>>. Each of
     * these Node<T> elements in the List can have children. The getChildren()
     * method will return the children of a Node<T>.
     * @return the children of Node<T>
     */
    public List<Node<T>> getChildren() {
        if (this.children == null) {
            return new ArrayList<Node<T>>();
        }
        return this.children;
    }
 
    /**
     * Sets the children of a Node<T> object. See docs for getChildren() for
     * more information.
     * @param children the List<Node<T>> to set.
     */
    public void setChildren(List<Node<T>> children) {
        this.children = children;
    }
 
    /**
     * Returns the number of immediate children of this Node<T>.
     * @return the number of immediate children.
     */
    public int getNumberOfChildren() {
        if (children == null) {
            return 0;
        }
        return children.size();
    }
    
    /**
     * Returns true if the node has any children. False otherwise.
     * @return a boolean signifying whether the node has children or not.
     */
    public boolean hasChildren() {
        if ((children == null) || (children.isEmpty()))
            return false;
        else
            return true;
    }
     
    /**
     * Adds a child to the list of children for this Node<T>. The addition of
     * the first child will create a new List<Node<T>>.
     * @param child a Node<T> object to set.
     */
    public void addChild(Node<T> child) {
        if (children == null) {
            children = new ArrayList<Node<T>>();
        }
        children.add(child);
    }
     
    /**
     * Inserts a Node<T> at the specified position in the child list. Will     * throw an ArrayIndexOutOfBoundsException if the index does not exist.
     * @param index the position to insert at.
     * @param child the Node<T> object to insert.
     * @throws IndexOutOfBoundsException if thrown.
     */
    public void insertChildAt(int index, Node<T> child) throws IndexOutOfBoundsException {
        if (index == getNumberOfChildren()) {
            // this is really an append
            addChild(child);
            return;
        } else {
            children.get(index); //just to throw the exception, and stop here
            children.add(index, child);
        }
    }
     
    /**
     * Remove the Node<T> element at index index of the List<Node<T>>.
     * @param index the index of the element to delete.
     * @throws IndexOutOfBoundsException if thrown.
     */
    public void removeChildAt(int index) throws IndexOutOfBoundsException {
        children.remove(index);
    }
    
    /**
     * In case the object is used in other trees, this method can be used
     * to clear any child references.
     */
    public void clear()
    {
        if (this.children != null)
            children.clear();
    }
    
    // removed to avoid confusion about how to use this class
/* 
    public T getData() {
        System.out.println("Node get data : " + data);
        return this.data;
    }
 
    public void setData(T data) {
        System.out.println("Node set data : " + data);
        this.data = data;
    }
*/    
/*    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("{").append(this.toString()).append(",[");
        int i = 0;
        for (Node<T> e : getChildren()) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(e.toString());
            i++;
        }
        sb.append("]").append("}");
        return sb.toString();

    }
*/
}
