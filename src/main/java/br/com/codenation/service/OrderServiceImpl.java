package br.com.codenation.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import br.com.codenation.model.OrderItem;
import br.com.codenation.model.Product;
import br.com.codenation.repository.ProductRepository;
import br.com.codenation.repository.ProductRepositoryImpl;

import static java.util.stream.Collectors.*;

public class OrderServiceImpl implements OrderService {

	private ProductRepository productRepository = new ProductRepositoryImpl();

	/**
	 * Calculate the sum of all OrderItems
	 */
	@Override
	public Double calculateOrderValue(List<OrderItem> items) {

		return items.stream()
				.filter(o-> productRepository.findById(o.getProductId()).isPresent())
				.mapToDouble(o-> productRepository.findById(o.getProductId())
								.map(p->p.getIsSale() ? p.getValue()*0.8 : p.getValue())
								.orElse(0.0)*o.getQuantity())
				.sum();
	}

	/**
	 * Map from idProduct List to Product Set
	 */
	@Override
	public Set<Product> findProductsById(List<Long> ids) {

		return productRepository
				.findAll()
				.stream()
				.filter(product -> ids.contains(product.getId()))
				.collect(toSet());
	}

	/**
	 * Calculate the sum of all Orders(List<OrderItem>)
	 */
	@Override
	public Double calculateMultipleOrders(List<List<OrderItem>> orders) {

		return orders
				.stream()
				.map(o -> calculateOrderValue(o))
				.reduce((o1,o2)->o1+o2)
				.get();
	}

	/**
	 * Group products using isSale attribute as the map key
	 */
	@Override
	public Map<Boolean, List<Product>> groupProductsBySale(List<Long> productIds) {

		return findProductsById(productIds)
				.stream()
				.collect(groupingBy(Product::getIsSale));

	}

}