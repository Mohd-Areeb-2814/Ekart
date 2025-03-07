package com.infy.ekart.payment.service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infy.ekart.payment.dto.CardDTO;
import com.infy.ekart.payment.dto.TransactionDTO;
import com.infy.ekart.payment.dto.TransactionStatus;
import com.infy.ekart.payment.entity.Card;
import com.infy.ekart.payment.entity.Transaction;
import com.infy.ekart.payment.exception.EKartPaymentException;
import com.infy.ekart.payment.exception.PayOrderFallbackException;
import com.infy.ekart.payment.repository.CardRepository;
import com.infy.ekart.payment.repository.TransactionRepository;
import com.infy.ekart.payment.utility.HashingUtility;


@Service(value="paymentService")
@Transactional
public class PaymentServiceImpl implements PaymentService{

	@Autowired
	private CardRepository cardRepository;
    
	@Autowired
	private TransactionRepository  transactionRepository;
	
	@Override
	public Integer addCustomerCard(String customerEmailId, CardDTO cardDTO) throws EKartPaymentException, NoSuchAlgorithmException {
		
		List<Card> listOfCustomerCards = cardRepository.findByCustomerEmailId(customerEmailId);
		if(listOfCustomerCards.isEmpty())
			throw new EKartPaymentException("PaymentService.CUSTOMER_NOT_FOUND");
		cardDTO.setHashCvv(HashingUtility.getHashValue(cardDTO.getCvv().toString()));
		Card newCard = new Card();
		newCard.setCardId(cardDTO.getCardId());
		newCard.setNameOnCard(cardDTO.getNameOnCard());;
		newCard.setCardNumber(cardDTO.getCardNumber());
		newCard.setCardType(cardDTO.getCardType());
		newCard.setExpiryDate(cardDTO.getExpiryMonth()+"-"+cardDTO.getExpiryYear());
		newCard.setCvv(cardDTO.getHashCvv());
		newCard.setCustomerEmailId(cardDTO.getCustomerEmailId());;
		
		cardRepository.save(newCard);
		return newCard.getCardID();
	}

	@Override
	public void updateCustomerCard(CardDTO cardDTO) throws EKartPaymentException, NoSuchAlgorithmException {
	
		
		Optional<Card> optionalCard = cardRepository.findById(cardDTO.getCardId());
		Card card = optionalCard.orElseThrow(() -> new EKartPaymentException("PaymentService.CARD_NOT_FOUND"));
		cardDTO.setHashCvv(HashingUtility.getHashValue(cardDTO.getCvv().toString()));
		card.setCardId(cardDTO.getCardId());
		card.setNameOnCard(cardDTO.getNameOnCard());
		card.setCardNumber(cardDTO.getCardNumber());
		card.setCardType(cardDTO.getCardType());
		card.setCvv(cardDTO.getHashCvv());
		//card.setCvv(cardDTO.getCvv());
		card.setExpiryDate(cardDTO.getExpiryMonth()+"-"+cardDTO.getExpiryYear());
		card.setCustomerEmailId(cardDTO.getCustomerEmailId());
		cardRepository.save(card);
		
	}

	@Override
	public void deleteCustomerCard(String customerEmailId, Integer cardId) throws EKartPaymentException {
		
		System.out.println(cardRepository);
		List<Card> listOfCustomerCards = cardRepository.findByCustomerEmailId(customerEmailId);
		if(listOfCustomerCards.isEmpty())
			throw new EKartPaymentException("PaymentService.CUSTOMER_NOT_FOUND");
		
		Optional<Card> optionalCards = cardRepository.findById(cardId);
		Card card = optionalCards.orElseThrow(() -> new EKartPaymentException("PaymentService.CARD_NOT_FOUND"));
		cardRepository.delete(card);
		
	}

	@Override
	public  CardDTO getCard(Integer cardId) throws EKartPaymentException{
		
		Optional<Card> optionalCards = cardRepository.findById(cardId);
		Card card = optionalCards.orElseThrow(() -> new EKartPaymentException("PaymentService.CARD_NOT_FOUND"));
		CardDTO cardDTO = new CardDTO();
		cardDTO.setCardId(card.getCardID());
		cardDTO.setNameOnCard(card.getNameOnCard());
		cardDTO.setCardNumber(card.getCardNumber());
		cardDTO.setCardType(card.getCardType());
		cardDTO.setHashCvv(card.getCvv());
		String[] expiryDate = card.getExpiryDate().split("-");
		cardDTO.setExpiryMonth(expiryDate[1]);
		cardDTO.setExpiryYear(expiryDate[0]);
		cardDTO.setCustomerEmailId(card.getCustomerEmailId());
		return cardDTO;
	}

	@Override
	public List<CardDTO> getCustomerCardOfCardType(String customerEmailId, String cardType)
			throws EKartPaymentException {
		
		
		List<Card> cards = cardRepository.findByCustomerEmailIdAndCardType(customerEmailId, cardType);
		
		
		if(cards.isEmpty())
		{
			throw new EKartPaymentException("PaymentService.CARD_NOT_FOUND");
		}
		List<CardDTO> cardDTOs = new ArrayList<CardDTO>();
		for(Card card : cards)
		{   CardDTO cardDTO = new CardDTO();
			cardDTO.setCardId(card.getCardID());
			cardDTO.setNameOnCard(card.getNameOnCard());
			cardDTO.setCardNumber(card.getCardNumber());
			cardDTO.setCardType(card.getCardType());
			cardDTO.setHashCvv("XXX");
			String[] expiryDate = card.getExpiryDate().split("-");
			cardDTO.setExpiryMonth(expiryDate[1]);
			cardDTO.setExpiryYear(expiryDate[0]);
			cardDTO.setCustomerEmailId(card.getCustomerEmailId());
			
			 cardDTOs.add(cardDTO);
		}
		return cardDTOs;
	}

	@Override
	public Integer addTransaction(TransactionDTO transactionDTO) throws EKartPaymentException, PayOrderFallbackException {
		System.out.println("Inside addTransaction method");
		if(transactionDTO.getTransactionStatus().equals(TransactionStatus.TRANSACTION_FAILED))
		{
			throw new PayOrderFallbackException("Payment.TRANSACTION_FAILED_CVV_NOT_MATCHING");
		}
	    Transaction transaction = new Transaction();
	    transaction.setCardId(transactionDTO.getCard().getCardId());
	  
	    transaction.setOrderId(transactionDTO.getOrder().getOrderId());
	    transaction.setTotalPrice(transactionDTO.getTotalPrice());
	    transaction.setTransactionDate(transactionDTO.getTransactionDate());
	    transaction.setTransactionStatus(transactionDTO.getTransactionStatus());
	    Transaction transactionId = transactionRepository.save(transaction);
	    
	    System.out.println("Transaction Added Successfully having transaction ID: "+ transaction.getTransactionId());
		return transactionId.getTransactionId();
	}

	@Override
	public TransactionDTO authenticatePayment(String customerEmailId, TransactionDTO transactionDTO) throws EKartPaymentException, NoSuchAlgorithmException {
		System.out.println("Inside authenticatePayment method");
		System.out.println(transactionDTO.toString());
		if(! transactionDTO.getOrder().getCustomerEmailId().equals(customerEmailId))
        {	
			System.out.println("******ORDER_DOES_NOT_BELONGS**********");
			throw new EKartPaymentException("PaymentService.ORDER_DOES_NOT_BELONGS");
        	
        }
		
		if(! transactionDTO.getOrder().getOrderStatus().equals("PLACED") )
        {
			System.out.println("******TRANSACTION_ALREADY_DONE**********");
			throw new EKartPaymentException("PaymentService.TRANSACTION_ALREADY_DONE");
        	
        }
		CardDTO cardDTO = getCard(transactionDTO.getCard().getCardId());
		System.out.println("****** " + cardDTO.getHashCvv().equals(HashingUtility.getHashValue(transactionDTO.getCard().getCvv().toString())));
		System.out.println(HashingUtility.getHashValue(transactionDTO.getCard().getCvv().toString()));
		System.out.println("between authenticatePayment method");
		if(! cardDTO.getCustomerEmailId().matches(customerEmailId))
		{
			System.out.println("******CARD_DOES_NOT_BELONGS**********");
			throw new EKartPaymentException("PaymentService.CARD_DOES_NOT_BELONGS");
		}
		if(! cardDTO.getCardType().equals(transactionDTO.getOrder().getPaymentThrough()))
		{
			System.out.println("******PAYMENT_OPTION_SELECTED_NOT_MATCHING_CARD_TYPE**********");
			throw new EKartPaymentException("PaymentService.PAYMENT_OPTION_SELECTED_NOT_MATCHING_CARD_TYPE");
		}
		if(cardDTO.getHashCvv().equals(HashingUtility.getHashValue(transactionDTO.getCard().getCvv().toString())))
		{	
			System.out.println("******CVV Equal**********");
			transactionDTO.setTransactionStatus(TransactionStatus.TRANSACTION_SUCCESS);
		}
		else
		{	
			System.out.println("******CVV Not Equal**********");
			transactionDTO.setTransactionStatus(TransactionStatus.TRANSACTION_FAILED);
			
		}	
		System.out.println(transactionDTO.toString());
		return transactionDTO;
	}
}
