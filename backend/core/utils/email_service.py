# utils/email_service.py
import requests
from sendgrid import SendGridAPIClient
from sendgrid.helpers.mail import Mail, Email, To, Content
from django.conf import settings
import logging

logger = logging.getLogger(__name__)

class EmailService:
    def __init__(self):
        self.api_key = settings.SENDGRID_API_KEY
        self.from_email = settings.DEFAULT_FROM_EMAIL

    def send_email(self, to_email, subject, html_content):
        try:
            url = "https://api.sendgrid.com/v3/mail/send"
            headers = {
                "Authorization": f"Bearer {self.api_key}",
                "Content-Type": "application/json"
            }
            data = {
                "personalizations": [{
                    "to": [{"email": to_email}]
                }],
                "from": {"email": self.from_email},
                "subject": subject,
                "content": [{
                    "type": "text/html",
                    "value": html_content
                }]
            }
            
            response = requests.post(url, headers=headers, json=data)
            if response.status_code in [200, 201, 202]:
                logger.info(f"Email sent successfully to {to_email}")
                return True
            else:
                logger.error(f"SendGrid API error: {response.status_code} - {response.text}")
                return False
                
        except Exception as e:
            logger.error(f"Failed to send email: {str(e)}")
            return False

    def send_password_reset(self, to_email, reset_link):
        subject = "Reset Your Password"
        html_content = f"""
        <div style="font-family: Arial, sans-serif; padding: 20px;">
            <h2>Password Reset Request</h2>
            <p>You requested to reset your password. Click the link below to set a new password:</p>
            <p style="margin: 25px 0;">
                <a href="{reset_link}" 
                   style="background-color: #4CAF50; color: white; padding: 12px 25px; 
                          text-decoration: none; border-radius: 3px;">
                    Reset Password
                </a>
            </p>
            <p>If you didn't request this, you can safely ignore this email.</p>
            <p>This link will expire in 1 hour for security reasons.</p>
            <hr style="margin: 20px 0; border: none; border-top: 1px solid #eee;">
            <p style="color: #666; font-size: 12px;">
                This is an automated message, please do not reply.
            </p>
        </div>
        """
        return self.send_email(to_email, subject, html_content)

    def send_verification_email(self, to_email, verification_link):
        subject = "Verify Your Email"
        html_content = f"""
        <div style="font-family: Arial, sans-serif; padding: 20px;">
            <h2>Verify Your Email Address</h2>
            <p>Thank you for registering! Please verify your email address by clicking the link below:</p>
            <p style="margin: 25px 0;">
                <a href="{verification_link}" 
                   style="background-color: #4CAF50; color: white; padding: 12px 25px; 
                          text-decoration: none; border-radius: 3px;">
                    Verify Email
                </a>
            </p>
            <p>If you didn't create an account, you can safely ignore this email.</p>
            <hr style="margin: 20px 0; border: none; border-top: 1px solid #eee;">
            <p style="color: #666; font-size: 12px;">
                This is an automated message, please do not reply.
            </p>
        </div>
        """
        return self.send_email(to_email, subject, html_content)