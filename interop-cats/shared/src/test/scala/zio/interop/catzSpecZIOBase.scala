package zio.interop

import org.scalacheck.{ Arbitrary, Cogen, Gen }
import zio.{ IO, Managed, ZIO, ZManaged }

private[interop] trait catzSpecZIOBase extends catzSpecBase with catzSpecZIOBaseLowPriority with GenIOInteropCats {

  implicit def ioArbitrary[E: Arbitrary: Cogen, A: Arbitrary: Cogen]: Arbitrary[IO[E, A]] =
    Arbitrary(Gen.oneOf(genIO[E, A], genLikeTrans(genIO[E, A]), genIdentityTrans(genIO[E, A])))

  implicit def ioParArbitrary[R, E: Arbitrary: Cogen, A: Arbitrary: Cogen]: Arbitrary[ParIO[R, E, A]] =
    Arbitrary(Arbitrary.arbitrary[IO[E, A]].map(Par.apply))

  implicit def managedArbitrary[R, E: Arbitrary: Cogen, A: Arbitrary: Cogen]: Arbitrary[Managed[E, A]] =
    Arbitrary(Arbitrary.arbitrary[IO[E, A]].map(ZManaged.fromEffect))
}

private[interop] sealed trait catzSpecZIOBaseLowPriority { this: catzSpecZIOBase =>

  implicit def zioArbitrary[R: Cogen, E: Arbitrary: Cogen, A: Arbitrary: Cogen]: Arbitrary[ZIO[R, E, A]] =
    Arbitrary(Arbitrary.arbitrary[R => IO[E, A]].map(ZIO.accessM(_)))

  object polyZManagedArb {
    implicit def zManagedArbitrary[R: Cogen, E: Arbitrary: Cogen, A: Arbitrary: Cogen]: Arbitrary[ZManaged[R, E, A]] =
      Arbitrary(Arbitrary.arbitrary[R => IO[E, A]].map(ZManaged fromEffect ZIO.accessM(_)))
  }

}
