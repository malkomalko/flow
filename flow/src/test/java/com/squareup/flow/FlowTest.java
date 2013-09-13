package com.squareup.flow;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fest.assertions.core.Condition;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class FlowTest {
  private static final Flow.Listener EMPTY_LISTENER = new Flow.Listener() {
    @Override public void go(Backstack backstack, Flow.Direction direction) {
    }
  };

  private static final A a = new A();
  private static final B b = new B();
  private static final C c = new C();
  private static final D d = new D();
  private static final E e = new E();

  @Before
  public void setup() throws Exception {
  }

  @Test
  public void goBack() {
    Backstack backstack = backstack(c, b, a);
    Flow flow = flow(backstack);
    assertThat(flow.goBack()).isTrue();
    assertThat(flow.getBackstack()).isEqualTo(skip(backstack, 1));

    assertThat(flow.goBack()).isTrue();
    assertThat(flow.getBackstack()).isEqualTo(skip(backstack, 2));

    assertThat(flow.goBack()).isFalse();
    assertThat(flow.getBackstack()).isEqualTo(skip(backstack, 2));
  }

  @Test
  public void goUp() {
    Backstack backstack = backstack(e);
    Flow flow = flow(backstack);

    assertThat(flow.goUp()).isTrue();
    backstack = flow.getBackstack();
    assertThat(backstack).is(backstack(C.class, A.class));

    assertThat(flow.goUp()).isTrue();
    // Going up with a correct backstack should yield the same screen.
    assertThat(flow.getBackstack()).isEqualTo(skip(backstack, 1));

    assertThat(flow.goUp()).isFalse();
    assertThat(flow.getBackstack()).isEqualTo(skip(backstack, 1));
  }

  @Test
  public void testReplaceTo() throws Exception {

  }

  private static Backstack skip(Backstack backstack, int count) {
    Backstack.Builder builder = backstack.buildUpon();
    while (count-- > 0) {
      builder.pop();
    }
    return builder.build();
  }

  private Condition<Iterable<Backstack.Entry>> backstack(final Class<?>... screenTypes) {
    return new Condition<Iterable<Backstack.Entry>>() {
      @Override public boolean matches(Iterable<Backstack.Entry> entries) {
        int i = 0;
        for (Backstack.Entry entry : entries) {
          if (i > screenTypes.length) {
            return false;
          }

          if (screenTypes[i++] != entry.getScreen().getClass()) {
            return false;
          }
        }

        return true;
      }

      @Override public String toString() {
        return Arrays.toString(screenTypes);
      }
    };
  }

  private static Flow flow(Backstack backstack) {
    return new Flow(backstack, EMPTY_LISTENER);
  }

  private static Backstack backstack(Screen... screens) {
    List<Screen> list = Arrays.asList(screens);
    Collections.reverse(list);
    return Backstack.emptyBuilder().addAll(list).build();
  }

  private static class A implements Screen {
  }

  private static class B implements Screen, HasParent<A> {
    @Override public A getParent() {
      return new A();
    }
  }

  private static class C implements Screen, HasParent<A> {
    @Override public A getParent() {
      return new A();
    }
  }

  private static class D implements Screen, HasParent<C> {
    @Override public C getParent() {
      return new C();
    }
  }

  private static class E implements Screen, HasParent<C> {
    @Override public C getParent() {
      return new C();
    }
  }
}
