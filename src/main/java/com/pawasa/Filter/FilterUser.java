package com.pawasa.Filter;

import com.pawasa.model.Category;
import com.pawasa.model.Notification;
import com.pawasa.model.User;
import com.pawasa.repository.CategoryRepository;
import com.pawasa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class FilterUser implements Filter {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        Set<Category> categorySet;
        HttpSession session = req.getSession();
        categorySet = (Set<Category>) session.getAttribute("list_cate");
        Set<Notification> notificationSet = (Set<Notification>) session.getAttribute("noti");
        if (categorySet == null) {
            categorySet = categoryRepository.findParentCategory();
            session.setAttribute("list_cate", categorySet);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)&& notificationSet == null) {
            String email = auth.getName();
            User user = userRepository.findByEmail(email);
            notificationSet = user.getNotifications();
            req.setAttribute("user", user);
        }
        else {
            notificationSet = new HashSet<>();
        }
        session.setAttribute("list_notification", notificationSet);
        chain.doFilter(request, response);
    }
}
