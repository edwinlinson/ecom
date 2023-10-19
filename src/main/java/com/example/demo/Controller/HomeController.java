package com.example.demo.Controller;


import com.example.demo.Global.GlobalData;
import com.example.demo.Model.Product;
import com.example.demo.Model.User;
import com.example.demo.Service.UserServiceImpl;
import com.example.demo.ServiceImpl.ShoppingCartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Service.CategoryService;
import com.example.demo.Service.ProductService;

import java.security.Principal;
import java.util.List;


@Controller
public class HomeController {
	final
	CategoryService categoryService;

	final
	ProductService productService;
	@Autowired
	UserServiceImpl userService;


	@Autowired
	public BCryptPasswordEncoder passwordEncoder;
	@Autowired
	ShoppingCartServiceImpl cartService;


	public HomeController(CategoryService categoryService, ProductService productService) {
		this.categoryService = categoryService;
		this.productService = productService;
	}

	@GetMapping({"/", "/home"})
	public String home(Model model) {
		model.addAttribute("cartCount", GlobalData.cart.size());
		return "new-home";
	}

//	@GetMapping("/shop")
//	public String shop(Model model) {
//		model.addAttribute("categories", categoryService.getAllCategory());
//		model.addAttribute("products", productService.getAllProducts());
//		model.addAttribute("cartCount",GlobalData.cart.size());
//		return "new_shop";
//	}
	@GetMapping("/page/{pageNo}")
	public String getPage(@PathVariable ("pageNo") int pageNo, Model model){
		int pageSize = 6;
		Page<Product> page = productService.findPaginated(pageNo,pageSize);
		List<Product> products = page.getContent();
		model.addAttribute("categories",categoryService.getAllCategory());
		model.addAttribute("products" ,products);
		model.addAttribute("currentPage",pageNo);
		model.addAttribute("totalPages",page.getTotalPages());
		model.addAttribute("totalItems",page.getTotalElements());
		model.addAttribute("listProducts",products);
		return "new_shop";
	}
	@GetMapping("/shop")
	public String pageShop(Model model){
		return getPage(1,model);
	}


	@GetMapping("/shop/category/{id}")
	public String shopByCategory(Model model, @PathVariable int id) {
		model.addAttribute("categories", categoryService.getAllCategory());
		model.addAttribute("products", productService.getAllProductsByCategoryId(id));
		model.addAttribute("cartCount",GlobalData.cart.size());
		return "new_shop";
	}
//	@GetMapping("/serach")
//	public String searchProduct(@RequestParam("query")String query, Model model ){
//		List<Product> result = productService.serachProducts(query);
//		model.addAttribute("searchResults",searchResults);
//		return
//	}
@GetMapping("/shop/search")
public String searchProducts(@RequestParam(value = "searchTerm", required = false) String searchTerm, Model model) {
	List<Product> searchedProducts;
	System.out.println("IN search controller");
	if (searchTerm == null || searchTerm.isEmpty()) {
		searchedProducts = productService.getAllProducts();
	} else {
		searchedProducts = productService.searchProducts(searchTerm);
	}
	model.addAttribute("products", searchedProducts);

	return "fragments/product_list";
}
	@GetMapping("/addToWishList/{id}")
	public String wishList(@PathVariable long id, Principal principal, Model model){
//		userService.addToWishList(principal.getName(),id);
		List<Product> wishList = userService.getWishList(principal.getName());
		Product add = productService.getProductById(id).get();
		if (wishList.contains(add)){
			model.addAttribute("message","This product is already in your WishList");
		}else {
			userService.addToWishList(principal.getName(),id);
			model.addAttribute("message","Item added to wishList");
		}
		model.addAttribute("wishlist",wishList);
		return "wishlist";
	}
	@GetMapping("/removeFromWishlist/{id}")
	public String removeWish(@PathVariable long id,Principal principal, Model model){
		Product product = productService.getProductById(id).get();
		List<Product> wishList = userService.getWishList(principal.getName());
		if (wishList.contains(product)) {
			wishList.remove(product);
			model.addAttribute("message","Product Removed from wishlist");
		}
		userService.updateWishlist(principal.getName(),wishList);
		List<Product> newWish = userService.getWishList(principal.getName());
		model.addAttribute("wishlist",newWish);
		return "wishlist";
	}

	@GetMapping("/shop/viewproduct/{id}")
	public String viewProduct(Model model, @PathVariable long id) {
		model.addAttribute("product", productService.getProductById(id).get());
		if (!model.containsAttribute("error")) {
			model.addAttribute("error", null);
		}
		return "ProductView";
	}

	@GetMapping("/addAllToCart")
	public String addAllCart(Principal principal){
		if (principal == null){
			return "redirect:/login";
		}
		List<Product> wishlist = userService.getWishList(principal.getName());
		for (Product item : wishlist){
			if (item.getQty() >0){
				cartService.addItemToCart(item,1, principal.getName());
			}
		}
		userService.clearWishList(principal.getName());
		return "redirect:/cart";
	}
	@GetMapping("/cart/removeItem/{index}")
	public String cartItemRemove(@PathVariable int index){
		GlobalData.cart.remove(index);
		return  "redirect:/cart";
	}
}
