package io.vaan.letmespeak.service

import cats.effect.IO
import io.vaan.letmespeak.dto.Verb
import io.vaan.letmespeak.util.ResourceUtils.fetchVerbs

import scala.collection.mutable.ListBuffer
import scala.io.StdIn.readLine
import scala.util.{Failure, Random, Success, Try}

object PracticeConsole {
  def play(verbs: Seq[Verb]): Unit = {
    val (countRightAnswer, countAllAnswers, needToLearn) = mainCycle(verbs)

    println
    println(s"$countRightAnswer/$countAllAnswers")

    if (needToLearn nonEmpty) {
      println("Need to learn this verbs:")
      needToLearn.foreach(x => println(s"${x.word}\t${x.v1}\t${x.v2}\t${x.v3}"))
    }
  }

  private def mainCycle(verbs: Seq[Verb]): (Int, Int, ListBuffer[Verb]) = {
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

    (countRightAnswer, countAllAnswers, needToLearn)
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
}
