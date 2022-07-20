package com.dmm.task.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.dmm.task.data.entity.Tasks;
import com.dmm.task.data.repository.TasksRepository;
import com.dmm.task.form.TaskForm;
import com.dmm.task.service.AccountUserDetails;

@Controller
public class TaskController {

	@Autowired
	public TasksRepository repo;
	public LocalDate date;

	/**
	 * 投稿の一覧表示.
	 * 
	 * @param model モデル
	 * @return 遷移先
	 */
	@GetMapping("/main/edit/{id}") // タスク編集
	public String edit(Model model, @PathVariable Integer id) {
		Tasks task = repo.getById(id);// idからrepoを使って編集しようとしているtasksインスタンスを取得
		model.addAttribute("task", task);

		return "edit";
	}

	@PostMapping("/main/edit/{id}") // タスク編集
	public String post(Model model, @PathVariable Integer id, TaskForm taskForm) {

		Tasks task = repo.getById(id);// idからrepoを使って編集しようとしているtasksインスタンスを取得

		task.setTitle(taskForm.getTitle());// 元のタスクの値をにゅりょくされた値にすり替える
		task.setText(taskForm.getText());
		task.setDone(taskForm.isDone());

		repo.save(task);

		return "redirect:/main";
	}

	@GetMapping("/main/create/{date}")
	public String create(Model model, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		this.date = date;

		return "create";
	}

	@PostMapping("/main/create")
	@PreAuthorize("hasRole('ROLE_ADMIN')")

	public String postForm(@Validated TasksRepository taskRepository, TaskForm taskForm, BindingResult bindingResult,
			@AuthenticationPrincipal AccountUserDetails user, Model model) {

		Tasks task = new Tasks();

		task.setName(user.getName());
		task.setTitle(taskForm.getTitle());
		task.setText(taskForm.getText());
		task.setDone(taskForm.isDone());
		task.setDate(date);

		repo.save(task);

		return "redirect:/main";
	}

	@GetMapping("/main")
	public String calendar(Model model, @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {// カレンダー表示
		List<List<LocalDate>> matrix = new ArrayList<>();
		List<LocalDate> week = new ArrayList<>();
		//Map<LocalDate, Tasks> task = new HashMap<>();
		List<Tasks> taskInfos = repo.findAll();
		MultiValueMap<LocalDate, Tasks> task = new LinkedMultiValueMap<LocalDate, Tasks>();


		//Map<LocalDate,Tasks> taskInfos = repo.findAll();

		int firstWeek = 0;
		LocalDate day;
		LocalDate now;

		now = LocalDate.now(); // todays Localdate

		if (date == null) {
			day = LocalDate.now().withDayOfMonth(1);// 7月1のデータ
// main.htmlで＜＞ボタンが押されなかった⇒今月のカレンダーと判断
		} else {
			day = date;
			now = date;

			// main.htmlで＜＞ボタンが押された⇒前月 or 来月のカレンダーと判断
		}
		model.addAttribute("month", day.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));
		model.addAttribute("prev", day.minusMonths(1));
		model.addAttribute("next", day.plusMonths(1));

		Month day1 = day.getMonth();// 現在の月取得
		int firstDay = day.lengthOfMonth();// 31 of july
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
					task.add(day, taskInfos.get(j)); // 当該日に合致していたら追加
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
					task.add(day, taskInfos.get(j)); // 当該日に合致していたら追加
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
					task.add(day, taskInfos.get(j)); // 当該日に合致していたら追加
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
		System.out.println(task);
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
}