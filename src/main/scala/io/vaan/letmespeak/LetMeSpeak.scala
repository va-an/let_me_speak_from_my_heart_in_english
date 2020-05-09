package io.vaan.letmespeak


import scala.io.Source
import scala.io.StdIn.readLine
import scala.util.Random

object LetMeSpeak {

  def main(args: Array[String]): Unit = {
    val verbs = fetchVerbs
    var count = 0

    verbs.foreach(verb => {
      print(s"${verb.word} >> ")
      val answer = readLine

      val correctAnswer = s"${verb.v1} ${verb.v2} ${verb.v3}"
      val isCorrect = answer == correctAnswer

      if (isCorrect) {
        count += 1
        println(s"Yep, $count correct answers")
      } else println(s"Nope, correct asnwer is: ${correctAnswer}")
    })
  }

  case class Verb(
    word: String,
    v1: String,
    v2: String,
    v3: String
  )

  private def fetchVerbs: Seq[Verb] = {
    val verbs = Source
      .fromResource("verbs.dsv")
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
}
