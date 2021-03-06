/*
 * Copyright 2013 ROBUST project consortium
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package eu.robust.wp2.hadoop.graph.model;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.google.common.collect.ComparisonChain;
import org.apache.hadoop.io.WritableComparable;

/** A representation of an undirected edge */
public class UndirectedEdge implements WritableComparable<UndirectedEdge>, Cloneable {

  private Vertex first;
  private Vertex second;

  public UndirectedEdge() {}

  public UndirectedEdge(Vertex first, Vertex second) {
    if (first.id() < second.id()) {
      this.first = first;
      this.second = second;
    } else {
      this.first = second;
      this.second = first;
    }
  }

  public UndirectedEdge(long firstVertexID, long secondVertexID) {
    this(new Vertex(firstVertexID), new Vertex(secondVertexID));
  }

  @Override
  public void write(DataOutput out) throws IOException {
    first.write(out);
    second.write(out);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    first = new Vertex();
    first.readFields(in);
    second = new Vertex();
    second.readFields(in);
  }

  /** Returns true if the other instance is an edge containing the same vertices */
  @Override
  public boolean equals(Object o) {
    if (o instanceof UndirectedEdge) {
      UndirectedEdge other = (UndirectedEdge) o;
      if (first.equals(other.first) && second.equals(other.second)) {
        return true;
      }
    }
    return false;
  }

  public Vertex firstVertex() {
    return first;
  }

  public Vertex secondVertex() {
    return second;
  }

  @Override
  public int hashCode() {
    return first.hashCode() + 31 * second.hashCode();
  }

  @Override
  public String toString() {
    return first.id() + "," + second.id();
  }

  @Override
  public int compareTo(UndirectedEdge other) {
    return ComparisonChain.start()
        .compare(first, other.first)
        .compare(second, other.second).result();
  }

  @Override
  public UndirectedEdge clone() {
    return new UndirectedEdge(first.clone(), second.clone());
  }
}
