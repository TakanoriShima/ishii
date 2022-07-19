package com.dmm.task.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dmm.task.data.entity.Tasks;
import com.dmm.task.data.repository.TasksRepository;
import com.dmm.task.form.TaskForm;
import com.dmm.task.service.AccountUserDetails;

@Controller
public class TaskController {

	@Autowired
	private TasksRepository repo;
	public LocalDate date;

	/**
	 * 投稿の一覧表示.
	 * 
	 * @param model モデル
	 * @return 遷移先
	 */
	@RequestMapping("/main/edit/{id}") // タスク編集
	public String edit(Model model,TasksRepository tasksRepository) {

		Tasks edit = new Tasks();
		//String title = ((Tasks) tasksRepository).getTitle();
		edit.setTitle(((Tasks) repo).getTitle());
		edit.setText(((Tasks) repo).getText());
		edit.setDate(((Tasks) repo).getDate());
		edit.setDone(((Tasks) repo).isDone());
		System.out.println(edit);
		//System.out.println(title);
		repo.save(edit);
		//model.addAttribute("/main/edit/{id}", edit);

		return "redirect:/main/edit/{id}";
	}

	@GetMapping("/main/create/{date}")
	public String create(Model model, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		System.out.println("createのDATEは" + date);
		this.date = date;

		return "create";
	}

	@PostMapping("/main/create")
	public String postForm(@Validated TasksRepository taskRepository, TaskForm taskForm, BindingResult bindingResult,
			@AuthenticationPrincipal AccountUserDetails user,
			Model model/*
						 * , @PathVariable("date") /*@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate
						 * date
						 */) {

		Tasks task = new Tasks();
		task.setName(user.getName());
		task.setTitle(taskForm.getTitle());
		task.setText(taskForm.getText());
		task.setDate(date);
		System.out.println("ポストフォームのDATEは" + date); // model.addAttribute("tasks", task);
		repo.save(task);

		return "redirect:/main";
	}

	@GetMapping("/main")
	public String calendar(Model model) {// カレンダー表示
		List<List<LocalDate>> matrix = new ArrayList<>();
		List<LocalDate> week = new ArrayList<>();
		Map<LocalDate, Tasks> task = new HashMap<>();
		List<Tasks> taskInfos = repo.findAll();
		int firstWeek = 0;

		LocalDate day = LocalDate.now().withDayOfMonth(1);// 7月1のデータ
		Month day1 = day.getMonth(); // 現在の月取得
		int firstDay = day.lengthOfMonth();// 31 of july
		LocalDate now = LocalDate.now(); // todays Localdate
		int nowDay = now.getDayOfMonth(); // todays date
		now = now.minusDays(nowDay - 1);// like2022-07-01

		firstWeek = now.getDayOfWeek().getValue();// 1 mon 2 tue 3 wed...and its 5

		LocalDate preDateOfMonth = now.minusDays(firstWeek);// 前月分のLocalDate 6/26
		day = preDateOfMonth;// 2022-6-26

		for (int i = 0; i < firstDay; i++) {
			DayOfWeek dw = day.getDayOfWeek();
			week.add(day);
			for (int j = 0; j < taskInfos.size(); j++) {
				if (taskInfos.get(j).getDate().isEqual(day)) {
					task.put(day, taskInfos.get(j)); // 当該日に合致していたら追加
				}
			}

			day = day.plusDays(1);// adds everyoneday
			if (dw == DayOfWeek.SATURDAY) {
				// 土曜日になると1週間の終わりと判断し、リストに格納する
				matrix.add(week);

				// 同時に、次週のための新しいListを用意する（新たにnewする）
				week = new ArrayList<>();

			}

		}
		for (int i = 0; i < 7; i++) {
			DayOfWeek dw = day.getDayOfWeek();
			week.add(day);

			for (int j = 0; j < taskInfos.size(); j++) {

				if (taskInfos.get(j).getDate().isEqual(day)) {
					task.put(day, taskInfos.get(j)); // 当該日に合致していたら追加
				}
			}

			if (dw == DayOfWeek.SATURDAY) {
				// 土曜日になると1週間の終わりと判断し、リストに格納する
				matrix.add(week);
				// 同時に、次週のための新しいListを用意する（新たにnewする）
				week = new ArrayList<>();
			}

			day = day.plusDays(1);// adds everyoneday
		}
		for (int i = 0; i < 7; i++) {
			DayOfWeek dw = day.getDayOfWeek();
			Month lastWeek = day.getMonth();
			Month nowMonth = day1;
			week.add(day);

			for (int j = 0; j < taskInfos.size(); j++) {
				if (taskInfos.get(j).getDate().isEqual(day)) {
					task.put(day, taskInfos.get(j)); // 当該日に合致していたら追加
				}
			}

			day = day.plusDays(1);// adds everyoneday

			if (nowMonth != lastWeek && dw == DayOfWeek.SATURDAY) {
				// 土曜日かつ月が先週と変わった時に処理を終わる
				matrix.add(week);
				break;

			}

		}
		model.addAttribute("tasks", task);

		model.addAttribute("matrix", matrix);// calendarのデータ
		return "/main";
	}

	/**
	 * 投稿を作成.
	 * 
	 * @param postForm 送信データ
	 * @param user     ユーザー情報
	 * @return 遷移先
	 */

	/**
	 * 投稿を削除する
	 * 
	 * @param id 投稿ID
	 * @return 遷移先
	 */
	@PostMapping("/main/delete/{id}")
	public String delete(@PathVariable Integer id) {
		repo.deleteById(id);
		return "redirect:/main";
	}

	public String calendar(Model model, @RequestParam(name = "date", defaultValue = "") String date) {
	String	prev;
	String	next;
	// dateには、main.htmlで指定した「yyyy-MM-dd（例：2022-07-01など）」が入ってくる
		System.out.println("日付は" + date);

		if (date.isEmpty()) {
			// main.htmlで＜＞ボタンが押されなかった⇒今月のカレンダーと判断
		} else {
			//System.out.println("日付は" + date);
				date = "2022-06-01";
			// main.htmlで＜＞ボタンが押された⇒前月 or 来月のカレンダーと判断
		}
		return "redirect:/main";
	}

}