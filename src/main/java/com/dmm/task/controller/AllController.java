package com.dmm.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dmm.task.data.repository.TasksRepository;

@Controller
public class AllController {
	@Autowired
	private TasksRepository repo;

	@RequestMapping("/testcreate")
	public String index() {
	return "create";
	}
	
/*	@RequestMapping("/main/create")
	public String create(@Validated PostForm postForm, BindingResult bindingResult,
			@AuthenticationPrincipal AccountUserDetails user, Model model) {
		// バリデーションの結果、エラーがあるかどうかチェック
		if (bindingResult.hasErrors()) {
			// エラーがある場合は投稿登録画面を返す
			List<Posts> list = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
			model.addAttribute("posts", list);
			model.addAttribute("postForm", postForm);
			return "/main/create";
		}

		Posts post = new Posts();
		post.setName(user.getName());
		post.setTitle(postForm.getTitle());
		post.setText(postForm.getText());
		post.setDate(LocalDateTime.now());

		repo.save(post);

		return "redirect:/create";
	}
*/

	@RequestMapping("/testedit")
	public String test() {
		return "edit";
	}

	@RequestMapping("/testmain")
	public String main() {
		return "main";
	}

	@RequestMapping("/testlogin")
	public String login() {
		return "login";
	}

}
