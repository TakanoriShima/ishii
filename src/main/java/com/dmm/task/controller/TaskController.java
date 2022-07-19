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
	@PostMapping("/main/edit/{id}") // タスク編集
	public String edit(/*@Validated TasksRepository taskRepository,  TaskForm taskForm,
			BindingResult bindingResult, @AuthenticationPrincipal AccountUserDetails user,*/ Model model) {
		Tasks edit = new Tasks();
		// edit.setName(tasks.getName());
		edit.setTitle(((Tasks) repo).getTitle());
		edit.setText(((Tasks) repo).getText());
		edit.setDate(((Tasks) repo).getDate());
		edit.setDone(((Tasks) repo).isDone());
		System.out.println(edit);
		repo.save(edit);
		model.addAttribute("/main/edit/{id}", edit);

		return "redirect:/main/edit";
	}

	@GetMapping("/main/create/{date}")
	public String create(/*@Validated TasksRepository taskRepository,Tasks tasks, TaskForm taskForm, BindingResult bindingResult,
			@AuthenticationPrincipal AccountUserDetails user,*/Model model, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		System.out.println("createのDATEは"+date);
		this.date = date;// model.addAttribute("tasks", task);
	//	Tasks task = new Tasks();
	//	task.setName(user.getName());
	//	task.setTitle(taskForm.getTitle());
	//	task.setText(taskForm.getText());
	//	task.setDate(date);
		// model.addAttribute("tasks", task);
		// model.addAttribute("tasks", postForm);
	//repo.save(task);
	//return "redirect:/main";
			return "create";
	}


	@PostMapping("/main/create")
	public String postForm(@Validated TasksRepository taskRepository, TaskForm taskForm, BindingResult bindingResult,
			@AuthenticationPrincipal AccountUserDetails user, Model model/*, @PathVariable("date") /*@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date*/) {

		Tasks task = new Tasks();
		task.setName(user.getName());
		task.setTitle(taskForm.getTitle());
		task.setText(taskForm.getText());
		task.setDate(date);
		System.out.println("ポストフォームのDATEは"+date);		// model.addAttribute("tasks", task);
		// model.addAttribute("tasks", postForm);
		repo.save(task);

		return "redirect:/main";
	}

	@GetMapping("/main")
	public String calendar(Model model) {// カレンダー表示
		List<List<LocalDate>> matrix = new ArrayList<>();
		List<LocalDate> week = new ArrayList<>();
		Map<LocalDate, /*List<Tasks>*/ Tasks > task = new HashMap<>();

		List<Tasks> taskInfos = repo.findAll();
		int firstWeek = 0;

		LocalDate day = LocalDate.now().withDayOfMonth(1);//7月1のデータ
		Month day1 = day.getMonth(); // 現在の月取得
		int firstDay = day.lengthOfMonth();//31 of july
		//System.out.println("今月の一日は" + firstDay);
		LocalDate now = LocalDate.now(); //todays Localdate
		int nowDay = now.getDayOfMonth(); //todays date
		now = now.minusDays(nowDay - 1);// like2022-07-01
	//	System.out.println(now);

		
		firstWeek = now.getDayOfWeek().getValue();//1 mon 2 tue 3 wed...and its 5

		LocalDate preDateOfMonth = now.minusDays(firstWeek);// 前月分のLocalDate 6/26
// 曜日を表すDayOfWeekを取得し、上で取得したLocalDateに曜日の値（DayOfWeek#getValue)をマイナス
		day = preDateOfMonth;//2022-6-26 
		//System.out.println(day);

// 1週間分の処理

		for (int i = 0; i < firstDay; i++) {
			DayOfWeek dw = day.getDayOfWeek();
		//	 System.out.println(dw);
			week.add(day);
		//	System.out.println(week);
			for (int j = 0; j < taskInfos.size(); j++) {
				//task.put(week.get(j), taskInfos.get(j));
		    	//System.out.println("一番上のtaskInfosの日にち"+taskInfos.get(j).getDate());
		    	//System.out.println(taskInfos);
		    	//System.out.println("一番上のdayの日にち"+day);
			    if(taskInfos.get(j).getDate().isEqual(day)) {
			    	//System.out.println(taskInfos.get(j).getDate().isEqual(day));
			    	task.put(day, taskInfos.get(j)); // 当該日に合致していたら追加
			    }// task.get(day).addAll(taskInfos);//
			}

			// タスク処理
			/*for (int j = 0; j < taskInfos.size(); j++) {
				task.put(week.get(j), taskInfos.get(j));
				// task.get(day).addAll(taskInfos);//
			}*/
			day = day.plusDays(1);// adds everyoneday
//System.out.println(week);
			if (dw == DayOfWeek.SATURDAY) {
				// 土曜日になると1週間の終わりと判断し、リストに格納する
				matrix.add(week);

				// 同時に、次週のための新しいListを用意する（新たにnewする）
				week = new ArrayList<>();
				// System.out.println(matrix);

			}
			
		}
		for (int i = 0; i < 7; i++) {
			DayOfWeek dw = day.getDayOfWeek();
			// Month lastWeek = day.getMonth();
			// Month nowMonth = day1;
			// System.out.println(dw);
			week.add(day);
			for (int j = 0; j < taskInfos.size(); j++) {
				//task.put(week.get(j), taskInfos.get(j));
		    	//System.out.println("taskInfosの日にち"+taskInfos.get(j).getDate());
		    	//System.out.println(taskInfos);
		    	//System.out.println("真ん中dayの日にち"+day);
			    if(taskInfos.get(j).getDate().isEqual(day)) {
			    	//System.out.println(taskInfos.get(j).getDate().isEqual(day));
			    	task.put(day, taskInfos.get(j)); // 当該日に合致していたら追加
			    }// task.get(day).addAll(taskInfos);//
			}

			if (dw == DayOfWeek.SATURDAY) {
				// 土曜日になると1週間の終わりと判断し、リストに格納する
				matrix.add(week);
				// 同時に、次週のための新しいListを用意する（新たにnewする）
				week = new ArrayList<>();
			//	System.out.println(matrix);
			}
			/*for (int j = 0; j < taskInfos.size(); j++) {
				task.put(week.get(j), taskInfos.get(j));
				// task.get(day).addAll(taskInfos);//
			}*/
			day = day.plusDays(1);// adds everyoneday
		}
		for (int i = 0; i < 7; i++) {
			DayOfWeek dw = day.getDayOfWeek();
			Month lastWeek = day.getMonth();
			Month nowMonth = day1;
			week.add(day);
	    	//System.out.println(taskInfos.size());

			for (int j = 0; j < taskInfos.size(); j++) {
				//task.put(week.get(j), taskInfos.get(j));
		    	//System.out.println(taskInfos.get(j).getDate());
		    	//System.out.println(taskInfos);
		    	//System.out.println(day);
			    if(taskInfos.get(j).getDate().isEqual(day)) {
			    	//System.out.println(taskInfos.get(j).getDate().isEqual(day));
			    	task.put(day, taskInfos.get(j)); // 当該日に合致していたら追加
			    }// task.get(day).addAll(taskInfos);//
			}
			// System.out.println(taskInfos.size());
			day = day.plusDays(1);// adds everyoneday
			// System.out.println(day);

			if (nowMonth != lastWeek && dw == DayOfWeek.SATURDAY) {
				// 土曜日かつ月が先週と変わった時に処理を終わる
				matrix.add(week);
				break;

			}

		}
		model.addAttribute("tasks", task);// タスクとmain.htmlの紐付け？

		model.addAttribute("matrix", matrix);// calendarのデータ
		// List<Tasks> list = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		// Collections.reverse(list); //普通に取得してこちらの処理でもOK
		// model.addAttribute("tasks", list);
		// TaskForm postForm = new TaskForm();
		//System.out.println(task);

		// 逆順で投稿をすべて取得する

		// List<Tasks> list = repo.findAll(Sort.by(Sort.Direction.DESC, "date"));
		// Tasks list = new Tasks();

		// list.setTitle(list.getTitle());
		// list.setText(list.getText());
		// repo.save(list);

		// Collections.reverse(tasks); //普通に取得してこちらの処理でもOK
		// model.addAttribute("task", list);
		// PostForm postForm = new PostForm();
		// model.addAttribute("postForm", postForm);
		return "/main";
	}

	/**
	 * 投稿を作成.
	 * 
	 * @param postForm 送信データ
	 * @param user     ユーザー情報
	 * @return 遷移先
	 */
	/*
	 * @PostMapping("/main/create")//意味のない残骸 public String create(@Validated
	 * TaskForm postForm, BindingResult bindingResult,
	 * 
	 * @AuthenticationPrincipal AccountUserDetails user, Model model) { //
	 * バリデーションの結果、エラーがあるかどうかチェック if (bindingResult.hasErrors()) { //
	 * エラーがある場合は投稿登録画面を返す //List<Tasks> list =
	 * repo.findAll(Sort.by(Sort.Direction.DESC, "date"));
	 * //model.addAttribute("tasks", list); //model.addAttribute("tasks", postForm);
	 * return "/main/create"; } /* Tasks task = new Tasks();
	 * task.setName(task.getName()); task.setTitle(task.getTitle());
	 * task.setText(task.getText()); task.setDate(task.getDate()); repo.save(task);
	 * 
	 * model.addAttribute("tasks", task); //model.addAttribute("tasks", postForm);
	 * Tasks post = new Tasks(); post.setName(user.getName());
	 * post.setTitle(Tasks.getTitle()); post.setText(post.getText());
	 * post.setDate(LocalDateTime.now());
	 * 
	 * repo.save(post); model.addAttribute("tasks", post);
	 * //model.addAttribute("tasks", postForm); return "redirect:/main"; }
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
		// dateには、main.htmlで指定した「yyyy-MM-dd（例：2022-07-01など）」が入ってくる

		if (date.isEmpty()) {
			System.out.println("日付は"+ date);
			// main.htmlで＜＞ボタンが押されなかった⇒今月のカレンダーと判断
		} else {
			date = "2022-06-01";
			// main.htmlで＜＞ボタンが押された⇒前月 or 来月のカレンダーと判断
		}
		return "redirect:/main";
	}

}