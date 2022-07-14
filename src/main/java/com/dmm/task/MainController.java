package com.dmm.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	@GetMapping("/main")
	public String main(Model model) {
		

		final List<LocalDate> month = new ArrayList<>();

		LocalDate now = LocalDate.now();
		int nowMonth = now.getMonthValue();
		int nowDay = now.getDayOfMonth();
		int dayOfMonth = nowMonth;
		now = now.minusDays(nowDay - 1);
		month.add(now);
		int firstWeek = now.getDayOfWeek().getValue();

		while (dayOfMonth == nowMonth) {
			now = now.plusDays(1L);
			dayOfMonth = now.getMonthValue();
			if (dayOfMonth != nowMonth) {
				break;
			}
			if (now.getDayOfMonth() < 10) {
				if (now.getDayOfMonth() == nowDay) {
					System.out.print(" *" + now.getDayOfMonth());
				} else {
					// System.out.print(" " + now.getDayOfMonth());
				}
			} else {
				if (now.getDayOfMonth() == nowDay) {
					// System.out.print(" *" + now.getDayOfMonth());
				} else {
					// System.out.print(" " + now.getDayOfMonth());
				}
			}
			int week = now.getDayOfWeek().getValue();
			month.add(now);

		}
		model.addAttribute("main", month);
		return "main";
	}
}