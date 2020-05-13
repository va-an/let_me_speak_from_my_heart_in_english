package io.vaan.letmespeak

import cats.effect.IO
import io.vaan.letmespeak.dto.Verb
import io.vaan.letmespeak.util.ResourceUtils.fetchVerbs

import scala.collection.mutable.ListBuffer
import scala.io.StdIn.readLine
import scala.util.{Failure, Random, Success, Try}

object LetMeSpeakConsole {
  def main(args: Array[String]): Unit = {
    val filename = args(0)
    val allVerbs = fetchVerbs(filename)

    val from = Try(args(1).toInt) match {
      case Failure(_) => 0
      case Success(value) => value
    }

    val to = Try(args(2).toInt) match {
      case Failure(_) => allVerbs.size
      case Success(value) => value
    }

    val verbs = Random.shuffle(
      allVerbs.slice(from, to)
    )

    practice(verbs)
  }

  def practice(verbs: Seq[Verb]): Unit = {
    val needToLearn = new ListBuffer[Verb]()
    var countRightAnswer = 0
    var countAllAnswers = 0

    verbs.foreach(verb => {
      countAllAnswers += 1

      val isCorrect = (for {
        _ <- printQuestion(verb.word)
        answer <- fetchAnswer()
        isCorrect <- checkAnswer(answer, verb)
      } yield isCorrect).unsafeRunSync()

      if (isCorrect) {
        countRightAnswer += 1
        println(s"Yep, $countRightAnswer correct answers from $countAllAnswers")
      } else {
        println(s"Nope, correct answer is: ${verb.v1} ${verb.v2} ${verb.v3}")
        needToLearn.addOne(verb)
      }
    })

    (for {
      _ <- showPraciceResult(countRightAnswer, countAllAnswers)
      _ <- showNeedToLearn(needToLearn.toList)
    } yield ()).unsafeRunSync()
  }

  def printQuestion(word: String): IO[Unit] = IO(print(s"$word >> "))

  def fetchAnswer(): IO[String] = IO(readLine)

  def checkAnswer(answer: String, expectedVerb: Verb): IO[Boolean] =
    Try {
      val splitAnswer = answer.split(" ")
      val isV1Right = expectedVerb.v1.split("/").contains(splitAnswer(0))
      val isV2Right = expectedVerb.v2.split("/").contains(splitAnswer(1))
      val isV3Right = expectedVerb.v3.split("/").contains(splitAnswer(2))

      isV1Right && isV2Right && isV3Right
    } match {
      case Failure(_) => IO.pure(false)
      case Success(result) => IO.pure(result)
    }

  def showPraciceResult(countRightAnswer: Int, countAllAnswers: Int): IO[Unit] =
    IO(println(s"\n$countRightAnswer/$countAllAnswers"))

  def showNeedToLearn(needToLearn: List[Verb]): IO[Unit] = {
    IO {
      if (needToLearn nonEmpty) {
        println("Need to learn this verbs:")
        needToLearn.foreach(x => println(s"${x.word}\t${x.v1}\t${x.v2}\t${x.v3}"))
      }
    }
  }
}
