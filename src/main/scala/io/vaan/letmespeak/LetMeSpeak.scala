package io.vaan.letmespeak


import scala.io.Source
import scala.io.StdIn.readLine
import scala.util.Random

object LetMeSpeak {
  private case class Verb(
    word: String,
    v1: String,
    v2: String,
    v3: String
  )

  private def fetchVerbs(fileForOpen: String): Seq[Verb] = {
    val verbs = Source
      .fromResource(fileForOpen)
      .getLines
      .toSeq
      .map { x =>
        val split = x.split("\\|")

        Verb(
          word = split(3),
          v1 = split(0),
          v2 = split(1),
          v3 = split(2))
      }

    Random.shuffle(verbs)
  }

  private val fileForOpen = "verbs-top-50.dsv"

  def main(args: Array[String]): Unit = {
    val verbs = fetchVerbs(fileForOpen)

    var countRightAnswer = 0
    var countAllAnswers = 0

    verbs.foreach(verb => {
      countAllAnswers += 1

      print(s"${verb.word} >> ")
      val answer = readLine

      val correctAnswer = s"${verb.v1} ${verb.v2} ${verb.v3}"
      val isCorrect = answer == correctAnswer

      if (isCorrect) {
        countRightAnswer += 1
        println(s"Yep, $countRightAnswer correct answers from $countAllAnswers")
      } else println(s"Nope, correct answer is: $correctAnswer")
    })
  }
}
