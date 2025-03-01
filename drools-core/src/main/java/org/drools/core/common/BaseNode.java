/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.common;

import java.util.Collection;

import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.RuleRemovalContext;
import org.drools.core.reteoo.Sink;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.util.Bag;
import org.kie.api.definition.rule.Rule;

/**
 * The base class for all Rete nodes.
 */
public abstract class BaseNode
    implements
    NetworkNode {

    protected int                 id;
    protected int                 memoryId = -1;
    protected RuleBasePartitionId partitionId;
    protected boolean             partitionsEnabled;
    protected Bag<Rule>           associations;
    private   boolean             streamMode;

    protected int                 hashcode;

    public BaseNode() {

    }

    /**
     * All nodes have a unique id, set in the constructor.
     *
     * @param id
     *      The unique id
     */
    public BaseNode(final int id,
                    final RuleBasePartitionId partitionId,
                    final boolean partitionsEnabled) {
        super();
        this.id = id;
        this.partitionId = partitionId;
        this.partitionsEnabled = partitionsEnabled;
        this.associations = new Bag<>();
    }

    /* (non-Javadoc)
     * @see org.kie.spi.ReteooNode#getId()
     */
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMemoryId() {
        if (memoryId < 0) {
            throw new UnsupportedOperationException();
        }
        return memoryId;
    }

    protected void initMemoryId( BuildContext context ) {
        if (context != null && this instanceof MemoryFactory) {
            memoryId = context.getNextMemoryId();
        }
    }

    public boolean isStreamMode() {
        return this.streamMode;
    }

    protected void setStreamMode(boolean streamMode) {
        this.streamMode = streamMode;
    }

    /**
     * Attaches the node into the network. Usually to the parent <code>ObjectSource</code> or <code>TupleSource</code>
     */
    public void attach(BuildContext context) {
        // do common shared code here, so it executes for all nodes
        doAttach(context);
        // do common shared code here, so it executes for all nodes
    }

    public void doAttach(BuildContext context) {

    }


    /**
     * A method that is called for all nodes whose network below them
     * changed, after the change is complete, providing them with an opportunity
     * for state update
     */
    public abstract void networkUpdated(UpdateContext updateContext);

    public boolean remove(RuleRemovalContext context,
                          ReteooBuilder builder) {
        boolean removed = doRemove( context, builder );
        if ( !this.isInUse() && !(this instanceof EntryPointNode) ) {
            builder.releaseId(this);
        }
        return removed;
    }

    /**
     * Removes the node from the network. Usually from the parent <code>ObjectSource</code> or <code>TupleSource</code>
     */
    protected abstract boolean doRemove(RuleRemovalContext context,
                                        ReteooBuilder builder);

    /**
     * Returns true in case the current node is in use (is referenced by any other node)
     */
    public abstract boolean isInUse();

    public abstract ObjectTypeNode getObjectTypeNode();

    public String toString() {
        return "[" + this.getClass().getSimpleName() + "(" + this.id + ")]";
    }

    /**
     * Returns the partition ID for which this node belongs to
     */
    public RuleBasePartitionId getPartitionId() {
        return this.partitionId;
    }

    /**
     * Sets the partition this node belongs to
     */
    public void setPartitionId(BuildContext context, RuleBasePartitionId partitionId) {
        this.partitionId = partitionId;
    }

    public void setPartitionsEnabled( boolean partitionsEnabled ) {
        this.partitionsEnabled = partitionsEnabled;
    }

    /**
     * Associates this node with the give rule
     */
    public void addAssociation( Rule rule ) {
        this.associations.add( rule );
    }

    public void addAssociation( BuildContext context, Rule rule ) {
        addAssociation( rule );
    }

    /**
     * Removes the association to the given rule from the
     * associations map.
     */
    public boolean removeAssociation( Rule rule ) {
        return this.associations.remove(rule);
    }

    public int getAssociationsSize() {
        return this.associations.size();
    }

    public Rule[] getAssociatedRules() {
        return this.associations.toArray( new Rule[this.associations.getKeySize()] );
    }

    public int getAssociatedRuleSize() {
        return this.associations.getKeySize();
    }

    public int getAssociationsSize(Rule rule) {
        return this.associations.sizeFor(rule);
    }

    public boolean isAssociatedWith( Rule rule ) {
        return this.associations.contains( rule );
    }

    @Override
    public final int hashCode() {
        return hashcode;
    }

    public Sink[] getSinks() {
        Sink[] sinks = null;
        if (this instanceof EntryPointNode ) {
            EntryPointNode source = (EntryPointNode) this;
            Collection<ObjectTypeNode> otns = source.getObjectTypeNodes().values();
            sinks = otns.toArray(new Sink[otns.size()]);
        } else if (this instanceof ObjectSource ) {
            ObjectSource source = (ObjectSource) this;
            sinks = source.getObjectSinkPropagator().getSinks();
        } else if (this instanceof LeftTupleSource ) {
            LeftTupleSource source = (LeftTupleSource) this;
            sinks = source.getSinkPropagator().getSinks();
        }
        return sinks;
    }
}
