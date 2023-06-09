package rcs.feyn.utils;

@FunctionalInterface
public interface HexaConsumer<T, U, V, W, X, Y> {
  
  void accept(T t, U u, V v, W w, X x, Y y);
}
