public sealed interface Val permits Val.Lam, Val.Neutral {
  public Val apply(Val that);

  public record Lam(Env env, String name, Expr body) implements Val {
    public Val apply(Val that) { return Expr.eval(Env.cons(that,env), body); }
  }

  public sealed interface Neutral extends Val permits Var, App {
    default public Val apply(Val that) { return new Val.App(this,that); }
  }

  public record Var(int level) implements Neutral {}
  public record App(Neutral f, Val arg) implements Neutral {}
}
