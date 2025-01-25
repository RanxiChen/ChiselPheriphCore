package alientek

import chisel3._
import chisel3.util._

import _root_.circt.stage.ChiselStage

class blinkled extends Module {
  val io = IO(new Bundle {
    val led = Output(UInt(4.W))
  }
  )

  /**
   * alientek board has 4 LEDs 
   * and sys_clk = 50MHz.
   * This module will counter time every seconds.
   */

    val num = RegInit(0.U(4.W))
    val second_clk_cnt_max = 50000000

    val second_clk_cnt = RegInit(0.U(32.W))

    second_clk_cnt := Mux(second_clk_cnt === ( second_clk_cnt_max-1).U,0.U,second_clk_cnt+1.U)

    num := Mux(second_clk_cnt === ( second_clk_cnt_max-1).U,num+1.U,num)

    io.led := num
}

class blinkledtop extends RawModule {
  val clk = IO(Input(Clock()))
  val rst_n = IO(Input(Bool()))
  val led = IO(Output(UInt(4.W)))

  withClockAndReset(clk,~rst_n) {
    val blinkled = Module(new blinkled)
    led := blinkled.io.led
  }

}

object blinkled extends App {
  ChiselStage.emitSystemVerilogFile(
    new blinkledtop,
    Array(
      "--target-dir","build/blinkled"
    )
  )
}

