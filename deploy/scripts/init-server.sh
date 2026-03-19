#!/bin/bash

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

check_root() {
    if [[ $EUID -ne 0 ]]; then
        log_error "This script must be run as root"
        exit 1
    fi
}

update_system() {
    log_info "Updating system packages..."
    apt update && apt upgrade -y
    log_info "System updated successfully"
}

install_docker() {
    log_info "Installing Docker..."
    
    if command -v docker &> /dev/null; then
        log_warn "Docker is already installed"
        return
    fi
    
    curl -fsSL https://get.docker.com | sh
    
    systemctl enable docker
    systemctl start docker
    
    log_info "Docker installed successfully"
}

install_docker_compose() {
    log_info "Installing Docker Compose..."
    
    if command -v docker-compose &> /dev/null; then
        log_warn "Docker Compose is already installed"
        return
    fi
    
    curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
    
    log_info "Docker Compose installed successfully"
}

install_nginx() {
    log_info "Installing Nginx..."
    
    if command -v nginx &> /dev/null; then
        log_warn "Nginx is already installed"
        return
    fi
    
    apt install nginx -y
    
    systemctl enable nginx
    systemctl start nginx
    
    log_info "Nginx installed successfully"
}

install_certbot() {
    log_info "Installing Certbot..."
    
    if command -v certbot &> /dev/null; then
        log_warn "Certbot is already installed"
        return
    fi
    
    apt install certbot python3-certbot-nginx -y
    
    log_info "Certbot installed successfully"
}

configure_firewall() {
    log_info "Configuring firewall..."
    
    if command -v ufw &> /dev/null; then
        ufw --force reset
        ufw default deny incoming
        ufw default allow outgoing
        ufw allow ssh
        ufw allow http
        ufw allow https
        ufw --force enable
        log_info "Firewall configured successfully"
    else
        log_warn "UFW not available, skipping firewall configuration"
    fi
}

create_project_directory() {
    log_info "Creating project directory..."
    
    mkdir -p /opt/smart-home
    mkdir -p /opt/backups
    mkdir -p /var/log/smart-home
    
    log_info "Project directories created"
}

setup_fail2ban() {
    log_info "Setting up fail2ban..."
    
    if ! command -v fail2ban-client &> /dev/null; then
        apt install fail2ban -y
    fi
    
    systemctl enable fail2ban
    systemctl start fail2ban
    
    log_info "Fail2ban configured successfully"
}

optimize_system() {
    log_info "Optimizing system settings..."
    
    cat >> /etc/sysctl.conf << EOF

# Smart Home App optimizations
net.core.somaxconn = 65535
net.ipv4.tcp_max_syn_backlog = 65535
net.core.netdev_max_backlog = 65535
net.ipv4.tcp_fin_timeout = 30
net.ipv4.tcp_keepalive_time = 300
net.ipv4.tcp_keepalive_probes = 5
net.ipv4.tcp_keepalive_intvl = 15
EOF
    
    sysctl -p
    
    log_info "System optimized successfully"
}

print_summary() {
    echo ""
    echo "=========================================="
    echo "  Server Initialization Complete!"
    echo "=========================================="
    echo ""
    echo "Installed components:"
    echo "  - Docker"
    echo "  - Docker Compose"
    echo "  - Nginx"
    echo "  - Certbot"
    echo "  - Fail2ban"
    echo ""
    echo "Next steps:"
    echo "  1. Clone your repository to /opt/smart-home"
    echo "  2. Configure environment variables in server/.env"
    echo "  3. Run: docker-compose up -d"
    echo "  4. Configure Nginx with your domain"
    echo "  5. Setup SSL certificates: certbot --nginx"
    echo ""
}

main() {
    log_info "Starting server initialization..."
    
    check_root
    update_system
    install_docker
    install_docker_compose
    install_nginx
    install_certbot
    configure_firewall
    create_project_directory
    setup_fail2ban
    optimize_system
    
    print_summary
    
    log_info "Server initialization completed successfully!"
}

main "$@"
