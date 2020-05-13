package io.vaan.letmespeak

import canoe.api._
import canoe.models.messages.{AnimationMessage, StickerMessage, TelegramMessage, TextMessage}
import canoe.syntax._
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.functor._
import fs2.Stream
import io.vaan.letmespeak.util.ResourceUtils.fetchVerbs

import scala.util.{Failure, Success, Try}

object LesMeSpeakBot extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val token: String = args.head

    val filename = Try(args(1)) match {
      case Failure(_) => "256.dsv"
      case Success(value) => value
    }

    val verbs = fetchVerbs(filename)

    Stream
      .resource(TelegramClient.global[IO](token))
      .flatMap { implicit client =>
        Bot.polling[IO].follow(
          start,
          stop,
          help
        )
      }
      .compile
      .drain
      .as(ExitCode.Success)
  }

  def start[F[_]: TelegramClient]: Scenario[F, Unit] =
    for {
      msg <- Scenario.expect(command("start"))
      _   <- Scenario.eval(msg.chat.send("started!"))
    } yield ()

  def stop[F[_]: TelegramClient]: Scenario[F, Unit] =
    for {
      msg <- Scenario.expect(command("stop"))
      _   <- Scenario.eval(msg.chat.send("stopped!"))
    } yield ()

  def help[F[_]: TelegramClient]: Scenario[F, Unit] =
    for {
      msg <- Scenario.expect(command("help"))
      _   <- Scenario.eval(msg.chat.send("help text"))
    } yield ()
}
