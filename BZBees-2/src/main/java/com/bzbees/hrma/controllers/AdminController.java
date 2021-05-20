package com.bzbees.hrma.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.bzbees.hrma.entities.Agency;
import com.bzbees.hrma.entities.Job;
import com.bzbees.hrma.entities.Notification;
import com.bzbees.hrma.entities.User;
import com.bzbees.hrma.services.AgencyService;
import com.bzbees.hrma.services.CompanyDocService;
import com.bzbees.hrma.services.JobService;
import com.bzbees.hrma.services.LikeService;
import com.bzbees.hrma.services.NotificationService;
import com.bzbees.hrma.services.PersonService;
import com.bzbees.hrma.services.ProfileImgService;
import com.bzbees.hrma.services.SocialMediaService;
import com.bzbees.hrma.services.TagService;
import com.bzbees.hrma.services.UserService;


@Controller
@RequestMapping("/admin")
@SessionAttributes({"agency", "agenciesList","person","userAccount","lastAgencyPicList",
	"agencyJobList","jobTagsList", "jobList","userNotifs","jobTagsList","userJobsInList","userJobsIdApplied"})
public class AdminController {
	
	@Autowired
	UserService userServ;
	
	@Autowired
	PersonService persServ;
	
	@Autowired
	AgencyService agencyServ;
	
	@Autowired
	ProfileImgService profileImgServ;
	
	@Autowired
	private CompanyDocService companyDocServ;
	
	@Autowired
	private SocialMediaService socialMediaServ;
	
	@Autowired
	BCryptPasswordEncoder bCryptEncoder;
	
	@Autowired
	JobService jobServ;
	
	@Autowired
	TagService tagServ;
	
	@Autowired
	NotificationService notifServ;
	
	@Autowired
	LikeService likeServ;
			
		@GetMapping("/dashboard")
		public String showDashboard(Model model, @ModelAttribute("message") String message, 
				Authentication auth, HttpSession session ) {
			if(auth !=null ) {
				String name = auth.getName();
				
				User user = (User) userServ.loadUserByUsername(name);
				
				if(user == null) {
					auth.setAuthenticated(false);
					return "/";
				}
				session.setAttribute("userAccount", user);
				
				
				//get authorities to string 			
				System.out.println("User's role in my home controller are: " + user.getRoles().toString());
				
				model.addAttribute("person", persServ.findPersonByUserId(user.getUserId()));
				model.addAttribute("userAccount", user);
				
					
				
				//get the agency if any is associated with current user 
				if(agencyServ.findAgencyByUserId(user.getUserId()) !=null) {
					Agency agency = agencyServ.findAgencyByUserId(user.getUserId());
					System.out.println("Agency in home controller " + agency.getAgencyName());
					model.addAttribute("agency", agency);
				} else {
					Agency agency = new Agency();
					model.addAttribute("agency", agency);
					System.out.println("THERE IS NO AGENCY");
				}
				
				//get all the agencies in a list 
				if(!agencyServ.findAll().isEmpty()) {
					List<Agency> allAgencies = agencyServ.findAll();
					System.out.println("adding agencies ---> " + allAgencies.size());
					model.addAttribute("agenciesList", allAgencies);
					Set<Long> userAgencyIdLiked = likeServ.findLikedAgencyIdsByUsername(auth.getName());
					model.addAttribute("userAgencyLiked",userAgencyIdLiked);
					
				} else {
					System.out.println("Empty agency list");
					model.addAttribute("userAgencyLiked", new HashSet<Long>());
					model.addAttribute("agenciesList", new ArrayList<Agency>());
				}
				
				//get all the jobs in a list when authenticated()
				if(!jobServ.getAll().isEmpty()) {
					List<Job> allJobs = jobServ.findJobsPostedByAgencies();
					model.addAttribute("jobList", allJobs);
					Set<Long> userJobsIdLiked = likeServ.findLikedJobsIdsByUsername(auth.getName());				
					model.addAttribute("userJobsLiked", userJobsIdLiked);				
					Set<Long> userJobsInList = jobServ.findJobsIdAddedToListByPersonId(user.getUserId());
					model.addAttribute("userJobsInList", userJobsInList);
					Set<Long> userJobsAppliedTo = jobServ.findJobsIdAppliedToByPersonId(user.getUserId());
					model.addAttribute("userJobsIdApplied", userJobsAppliedTo);
			
				} else {
					model.addAttribute("userJobsIdApplied", new HashSet<Long>());
					model.addAttribute("jobList", new ArrayList<Job>());
					model.addAttribute("userJobsLiked", new HashSet<Long>());
					model.addAttribute("userJobsInList", new HashSet<Long>());
				}
				
				//get the logged in user notifs
				if(!notifServ.findNotificationsByUserId(user.getUserId()).isEmpty()) {
					List<Notification> allUserNotif = notifServ.reverseFindNotificationsByUserId(user.getUserId());
					List<Notification> showUserNotifs = new ArrayList<>();
					int count = allUserNotif.size();
					for(int i = 0; i < 4; i++) {
					
						if(count == 0) {
							break;
						}
						showUserNotifs.add(allUserNotif.get(i));
						count = count -1;
						System.out.println(allUserNotif.get(i).getNotificationId() 
								+ " " + allUserNotif.get(i).getDateCreated());
					}

					model.addAttribute("userNotifs", showUserNotifs);
				} else {
					model.addAttribute("userNotifs", new ArrayList<>());
				}
			
			}
			
			return "admin/dashboard";
		}
		

	

	

}
