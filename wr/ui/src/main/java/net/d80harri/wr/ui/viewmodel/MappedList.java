package net.d80harri.wr.ui.viewmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
 










import com.sun.javafx.binding.ListExpressionHelper;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.ListBinding;
import javafx.beans.binding.ListExpression;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;
 
public class MappedList<E, F> extends TransformationList<E, F> {
 
    private final Function<F, E> extractorFunc;
    private final Function<E, F> constructorFunc;
    private final ObservableList<F> source;
    
    private final HashMap<E, F> extraction = new HashMap<E, F>();
    private final HashMap<F, E> constructions = new HashMap<F, E>();
 
    public MappedList(ObservableList<F> source, Function<F, E> mapper, Function<E, F> mapback) {
        super(source);
        this.extractorFunc = mapper;
        this.constructorFunc = mapback;
        this.source = source;
    }
    
//    public MappedList(ObservableList<F> source, Function<F, E> mapper) {
//		this(source, mapper, null);
//	}
    
    @Override
    public boolean add(E e) {
    	return source.add(construct(e));
    }
    
    @Override
    public E set(int index, E element) {
    	return extract(source.set(index, constructorFunc.apply(element)));
    }
 
    @Override
    public int getSourceIndex(int index) {
        return index;
    }
 
    @Override
    public E get(int index) {
        return extract(getSource().get(index));
    }
    
    @Override
    public boolean remove(Object o) {
    	return source.remove(construct((E) o));
    }
    
    @Override
    public E remove(int index) {
    	return extract(source.remove(index));
    }
 
    @Override
    public int size() {
        return getSource().size();
    }
    
    private E extract(F elem) {
    	E result = constructions.get(elem);
    	if (result == null) {
    		result = extractorFunc.apply(elem);
    		constructions.put(elem, result);
    		extraction.put(result, elem);
    	}
    	return result;
    }
    
    private F construct(E elem) {
    	F result = extraction.get(elem);
    	if (result == null) {
    		result = constructorFunc.apply(elem);
    		constructions.put(result, elem);
    		extraction.put(elem, result);
    	}
    	return result;
    }
 
    @Override
    protected void sourceChanged(Change<? extends F> c) {
        fireChange(new Change<E>(this) {
 
            @Override
            public boolean wasAdded() {
                return c.wasAdded();
            }
 
            @Override
            public boolean wasRemoved() {
                return c.wasRemoved();
            }
 
            @Override
            public boolean wasReplaced() {
                return c.wasReplaced();
            }
 
            @Override
            public boolean wasUpdated() {
                return c.wasUpdated();
            }
 
            @Override
            public boolean wasPermutated() {
                return c.wasPermutated();
            }
 
            @Override
            public int getPermutation(int i) {
            	
                return c.getPermutation(i);
            }
 
            @Override
            protected int[] getPermutation() {
                // This method is only called by the superclass methods
                // wasPermutated() and getPermutation(int), which are
                // both overriden by this class. There is no other way
                // this method can be called.
                throw new AssertionError("Unreachable code");
            }
 
            @Override
            public List<E> getRemoved() {
                ArrayList<E> res = new ArrayList<>(c.getRemovedSize());
                for(F e: c.getRemoved()) {
                    res.add(extractorFunc.apply(e));
                }
                return res;
            }
 
            @Override
            public int getFrom() {
                return c.getFrom();
            }
 
            @Override
            public int getTo() {
                return c.getTo();
            }
 
            @Override
            public boolean next() {
                return c.next();
            }
 
            @Override
            public void reset() {
                c.reset();
            }
        });
    }
}