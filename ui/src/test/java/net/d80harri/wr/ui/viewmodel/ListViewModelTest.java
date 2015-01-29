package net.d80harri.wr.ui.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;

import org.junit.Test;

public class ListViewModelTest {
	
	@Test
	public void simpleTest() throws Throwable {
		List<String> model = new ArrayList<String>();
		ListViewModel<String, Integer> vm = new ListViewModel<String, Integer>(model, i -> Integer.toString(i));
		
		vm.listProperty().getValue().add(1);
		assertThat(model).containsExactly("1");
		
		vm.listProperty().add(2);
		assertThat(model).containsExactly("1", "2");
		
		vm.listProperty().set(FXCollections.observableArrayList(1, 3, 5));
		assertThat(model).containsExactly("1", "3", "5");
		
		vm.listProperty().remove((Object)3);
		assertThat(model).containsExactly("1", "5");
		
		vm.listProperty().add(1, 3);
		assertThat(model).containsExactly("1", "3", "5");
		
		vm.listProperty().remove(1);
		assertThat(model).containsExactly("1", "5");
	}
	
	public String strModel;
	
	@Test
	public void test2() throws Throwable {
		List<String> viewModel = new ArrayList<String>();
		ObservableList<String> list = FXCollections.observableList(viewModel);
		ObservableList<Integer> listInt = new MappedList<Integer, String>(list, i -> Integer.parseInt(i));
		list.addListener((ListChangeListener.Change<? extends String> c) -> {System.out.println(viewModel);});
		listInt.addListener((ListChangeListener.Change<? extends Integer> c) -> {System.out.println(listInt);});

		list.add("1");
		list.add("2");
		list.setAll("3", "2");
		
		
		
		
	}
	
	public static class MappedList<E, F> extends TransformationList<E, F> {
		 
	    private final Function<F, E> mapper;
	 
	    public MappedList(ObservableList<? extends F> source, Function<F, E> mapper) {
	        super(source);
	        this.mapper = mapper;
	    }
	 
	    @Override
	    public int getSourceIndex(int index) {
	        return index;
	    }
	 
	    @Override
	    public E get(int index) {
	        return mapper.apply(getSource().get(index));
	    }
	 
	    @Override
	    public int size() {
	        return getSource().size();
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
	                    res.add(mapper.apply(e));
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
}
