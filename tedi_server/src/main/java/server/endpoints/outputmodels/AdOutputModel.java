package server.endpoints.outputmodels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdOutputModel {

	private Long id; //this is used to reference ad when wanting to apply
	private String title;
	private String description;
	private List<SkillOutputModel> skills;
	private UserOutputModel publisher;
	private String publishDate;
	
	public AdOutputModel() {
		skills = new ArrayList<>();
	}
	
	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public List<SkillOutputModel> getSkills() {
		return skills;
	}

	public UserOutputModel getPublisher() {
		return publisher;
	}
	
	public String getPublishDate() {
		return publishDate;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setSkills(List<SkillOutputModel> skills) {
		this.skills = skills;
	}

	public void addSkill(SkillOutputModel skill) {
		this.skills.add(skill);
	}
	
	public void setPublisher(UserOutputModel publisher) {
		this.publisher = publisher;
	}
	
	public void setPublishDate(Date publishDate) {
		if (publishDate == null) {
			this.publishDate = null;
			return;
		}
			
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		this.publishDate = sdf.format(publishDate);
	}
	
}
