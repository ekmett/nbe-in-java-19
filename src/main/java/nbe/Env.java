
public sealed interface Env permits Env.Nil, Env.Cons {

  public record Cons(Val head, Env tail) implements Env {}
  public record Nil() implements Env {}

  public static Env nil = new Nil();
  public static Env cons(Val x, Env xs) { return new Env.Cons(x,xs); }

  public static Val at(Env xs, int index) {
    while(xs instanceof Cons(Val y, Env ys)) {
      if (index-- == 0) return y;
      xs = ys;
    }
    throw new IllegalArgumentException("environment index out of bounds");
  }

  public static Env make(Val... vs) {
    Env r = Env.nil;
    for (int i=vs.length-1;i>=0;--i) 
      r = Env.cons(vs[i],r);
    return r;
  }
}
