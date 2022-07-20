// just so i don't have to carve this into a stupid number of files
public class Interpreter {

  public record Name(String name) {}

  public sealed interface Val
  permits Val.Lam, Val.Neutral {
    public Val apply(Val that);

    public record Lam(Env env, String name, Expr body) implements Val {
      public Val apply(Val that) { return Expr.eval(Env.cons(that,env), body); }
    }

    public sealed interface Neutral extends Val
    permits Var, App {
      default public Val apply(Val that) { return new Val.App(this,that); }
    }
    public record Var(int level) implements Neutral {}
    public record App(Neutral f, Val arg) implements Neutral {}
  }

  public sealed interface Env
  permits Env.Nil, Env.Cons {
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
  }

  public static Env env(Val... vs) {
    Env r = Env.nil;
    for (int i=vs.length-1;i>=0;--i) 
      r = Env.cons(vs[i],r);
    return r;
  }

  public sealed interface Expr permits Expr.Var, Expr.Lam, Expr.App {
    public record Var(int n) implements Expr {}
    public record App(Expr f, Expr x) implements Expr {}
    public record Lam(String n, Expr body) implements Expr {}

    public static Val eval(Env e, Expr t) {
      return switch (t) {
        case Var(int n) -> Env.at(e,n);
        case App(Expr f, Expr x) -> eval(e,f).apply(eval(e,x));
        case Lam(String n, Expr b) -> new Val.Lam(e,n,b);
      };
    }
    public static Expr uneval(int d, Val v) {
      return switch(v) {
        case Val.Var(int level) -> new Var(d - level - 1);
        case Val.App(Val.Neutral f, Val x) -> new App(uneval(d,f),uneval(d,x));
        case Val.Lam l -> {
          var x = new Val.Var(d); // l.name);
          var vb = eval(Env.cons(x,l.env),l.body);
          yield new Lam(l.name,uneval(d+1,vb));
        }
      };
    }
    // reduce a term to normal form
    public static Expr nf(Env e, Expr t) {
      return uneval(0,eval(e,t));
    }
  }

  public static Expr var(int n) { return new Expr.Var(n); }
  public static Expr lam(String x, Expr body) { return new Expr.Lam(x,body); }
  public static Expr app(Expr f, Expr x) { return new Expr.App(f,x); }

  public static void main(String args[]) {
    var I = Expr.eval(env(),lam("x",var(0)));
    var K = Expr.eval(env(),lam("x",lam("y",var(1))));
    var KI = Expr.nf(env(I,K),app(var(1),var(0)));
    System.out.printf("%s%n",KI);	
  }
}
