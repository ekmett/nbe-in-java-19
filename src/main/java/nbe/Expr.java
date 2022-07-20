// just so i don't have to carve this into a stupid number of files

public sealed interface Expr permits Expr.Var, Expr.Lam, Expr.App {
  public record Var(int n) implements Expr {}
  public record App(Expr f, Expr x) implements Expr {}
  public record Lam(String n, Expr body) implements Expr {}

  public static Val eval(Env e, Expr t) {
    return switch (t) {
      case Var(var n) -> Env.at(e,n);
      case App(var f, var x) -> eval(e,f).apply(eval(e,x));
      case Lam(var n, var b) -> new Val.Lam(e,n,b);
    };
  }

  public static Expr uneval(int d, Val v) {
    return switch(v) {
      case Val.Var(var k) -> new Var(d - k - 1);
      case Val.App(var f, var x) -> new App(uneval(d,f),uneval(d,x));
      case Val.Lam l -> {
        var x = new Val.Var(d);
        var vb = eval(Env.cons(x,l.env()),l.body());
        yield new Lam(l.name(),uneval(d+1,vb));
      }
    };
  }

  // reduce a term to normal form
  public static Expr nf(Env e, Expr t) {
    return uneval(0,eval(e,t));
  }

  public static Expr var(int n) { return new Expr.Var(n); }
  public static Expr lam(String x, Expr body) { return new Expr.Lam(x,body); }
  public static Expr app(Expr f, Expr x) { return new Expr.App(f,x); }

  public static void main(String args[]) {
    var I = eval(Env.make(),lam("x",var(0)));
    var K = eval(Env.make(),lam("x",lam("y",var(1))));
    var KI = nf(Env.make(I,K),app(var(1),var(0)));
    System.out.printf("%s%n",KI);	
  }
}
